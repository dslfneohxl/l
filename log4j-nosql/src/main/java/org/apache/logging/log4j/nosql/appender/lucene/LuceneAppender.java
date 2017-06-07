/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.nosql.appender.lucene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.util.Strings;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.store.FSDirectory;

/**
 * This Appender writes logging events to a lucene index library. It takes a list of
 * {@link IndexField} with which determines which fields are written to the index library.
 * <Lucene name="lucene" ignoreExceptions="true" target="/target/lucene/index">
 *    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
 *    
 *	  <IndexField name="time" pattern="%d{UNIX_MILLIS}" type="LongField"/>
 *	  <IndexField name="level" pattern="%-5level" />
 *	  <IndexField name="content" pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
 * </Lucene>
 */
@Plugin(name = "Lucene", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LuceneAppender extends AbstractAppender {
	/**
	 * index directory
	 */
	private final String target;
	/**
	 * Index expiration time (seconds)
	 */
	private final Integer expiryTime;
	/**
	 * IndexField array.
	 */
	private final LuceneIndexField[] indexFields;
	/**
	 * Periodically clear the index and submit the IndexWriter thread pool
	 */
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
	/**
	 * IndexWriter corresponding to each index directory.
	 */
	private static final ConcurrentHashMap<String, IndexWriter> writerMap = new ConcurrentHashMap<String, IndexWriter>();
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for(IndexWriter writer: writerMap.values()){
					if(null!=writer && writer.isOpen()){
						try {
							writer.commit();
							writer.close();
						} catch (IOException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
				writerMap.clear();
			}
		});
		
	}

	protected LuceneAppender(String name, boolean ignoreExceptions, Filter filter,
			Layout<? extends Serializable> layout, String target, Integer expiryTime, LuceneIndexField[] indexFields) {
		super(name, filter, layout, ignoreExceptions);
		this.target = target;
		this.expiryTime = expiryTime;
		this.indexFields = indexFields;
		
		initIndexWriter();
		registerCommitTimer();
		if (this.expiryTime != null){
			registerClearTimer();
		}
	}

	/**
	 * IndexWriter initialization.
	 */
	private IndexWriter initIndexWriter() {
		if (null == writerMap.get(target)) {
			try {
				FSDirectory fsDir = FSDirectory.open(Paths.get(this.target));
				IndexWriterConfig writerConfig = new IndexWriterConfig(new LuceneAnalyzer());
				writerMap.putIfAbsent(target, new IndexWriter(fsDir, writerConfig));
			} catch (IOException e) {
				LOGGER.error("IndexWriter initialization failed!" + e.getMessage(), e);
			}
		}
		return writerMap.get(target);
	}

	/**
	 * Register IndexWriter commit timertask.
	 */
	private void registerCommitTimer() {
		scheduledExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				IndexWriter indexWriter = initIndexWriter();
				if (null!=indexWriter && indexWriter.numRamDocs()>0) {
					try {
						indexWriter.commit();
					} catch (IOException e) {
						LOGGER.error("IndexWriter commit failed!"+e.getMessage(),e);
					}
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}
	
	/**
	 * Register IndexWriter clean timertask.
	 * Delete the index before {@link LuceneAppender#expiryTime} second every day at 0.
	 * 
	 * @see LuceneAppender#expiryTime
	 */
	private void registerClearTimer() {
		Calendar calendar = Calendar.getInstance();
		long curMillis = calendar.getTimeInMillis();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long difMinutes = (calendar.getTimeInMillis() - curMillis) / (1000 * 60);

		scheduledExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("delete index start!",target,expiryTime);
				IndexWriter indexWriter = initIndexWriter();
				if (null != indexWriter) {
					Long start = 0L;
					Long end = System.currentTimeMillis() - expiryTime * 1000;
					NumericRangeQuery<Long> rangeQuery = NumericRangeQuery.newLongRange("timestamp", start, end, true, true);
					try {
						indexWriter.deleteDocuments(rangeQuery);
						indexWriter.commit();
						LOGGER.info("delete index end! ",target,expiryTime);
					}catch (IOException e) {
						LOGGER.error("delete index failed! "+e.getMessage(),e);
					}
				}
			}
		}, difMinutes, 1440, TimeUnit.MINUTES);
	}

	/**
	 * create lucene index.
	 * 
	 * @param event
	 */
	@Override
	public void append(LogEvent event) {
		if (null != indexFields && indexFields.length > 0) {
			IndexWriter indexWriter = initIndexWriter();
			if(null != indexWriter){
				Document doc = new Document();
				doc.add(new LongField("timestamp", event.getTimeMillis(), Field.Store.YES));
				try {
					for (LuceneIndexField field : indexFields) {
						String value = field.getLayout().toSerializable(event);
						if (Strings.isEmpty(value) || value.matches("[$]\\{.+\\}")) {
							return;
						}
						value = value.trim();
						String type = field.getType();
						if (Strings.isNotEmpty(type)) {
							Class<?> clazz = Class.forName("org.apache.lucene.document." + type);
							if (clazz == LongField.class) {
								doc.add(new LongField(field.getName(), Long.valueOf(value), Field.Store.YES));
							} else if (clazz == StringField.class) {
								doc.add(new StringField(field.getName(), value, Field.Store.YES));
							} else if (clazz == TextField.class) {
								doc.add(new TextField(field.getName(), value, Field.Store.YES));
							} else {
								throw new UnsupportedOperationException(type + " type currently not supported.");
							}
						} else {
							doc.add(new TextField(field.getName(), value, Field.Store.YES));
						}
					}
					indexWriter.addDocument(doc);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					if (!ignoreExceptions()) {
						throw new AppenderLoggingException(e);
					}
				}
			}
		}
	}
	
	@PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
        implements org.apache.logging.log4j.core.util.Builder<LuceneAppender> {

        @PluginElement("IndexField")
        @Required(message = "No IndexField provided")
        private LuceneIndexField[] indexField;        

        @PluginBuilderAttribute        
        private Integer expiryTime;

        @PluginBuilderAttribute
        @Required(message = "No target provided")
        private String target;
        
        public B withTarget(final String target){
        	this.target = target;
        	return this.asBuilder();
        }
        
        public B withExpiryTime(final Integer expiryTime){
        	this.expiryTime = expiryTime;
        	return this.asBuilder();
        }
        
        public B withIndexField(final LuceneIndexField... indexField){
        	this.indexField = indexField;
        	return this.asBuilder();
        }        

        @Override
        public LuceneAppender build() {
            return new LuceneAppender(getName(), isIgnoreExceptions(),getFilter(), this.getLayout(),this.target,this.expiryTime,this.indexField);
        }        
    }	

	@Override
	public final void start() {
		if (null == writerMap.get(target)) {
			LOGGER.error("No IndexWriter set for the appender named [{}].", this.getName());
		}
		super.start();
	}

	@Override
	public boolean stop(final long timeout, final TimeUnit timeUnit) {
		setStopping();
		boolean stopped = super.stop(timeout, timeUnit, false);
		IndexWriter indexWriter = writerMap.get(target);
		if (null != indexWriter) {
			try {
				indexWriter.close();
				writerMap.remove(target);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		setStopped();
		return stopped;
	}
}
