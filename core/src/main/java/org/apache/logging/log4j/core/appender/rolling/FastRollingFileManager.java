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
package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;

/**
 * Extends RollingFileManager but instead of using a buffered output stream,
 * this class uses a {@code ByteBuffer} and a {@code RandomAccessFile} to do the
 * I/O.
 */
public class FastRollingFileManager extends RollingFileManager {
    static final int DEFAULT_BUFFER_SIZE = 256 * 1024;

    private static final FastRollingFileManagerFactory FACTORY = new FastRollingFileManagerFactory();

    private final boolean isImmediateFlush;
    private RandomAccessFile randomAccessFile;
    private final ByteBuffer buffer;
    private final ThreadLocal<Boolean> isEndOfBatch = new ThreadLocal<Boolean>();

    public FastRollingFileManager(final RandomAccessFile raf, final String fileName,
            final String pattern, final OutputStream os, final boolean append,
            final boolean immediateFlush, final long size, final long time,
            final TriggeringPolicy policy, final RolloverStrategy strategy,
            final String advertiseURI, final Layout layout) {
        super(fileName, pattern, os, append, size, time, policy, strategy, advertiseURI, layout);
        this.isImmediateFlush = immediateFlush;
        this.randomAccessFile = raf;
        isEndOfBatch.set(Boolean.FALSE);

        // TODO make buffer size configurable?
        buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    }

    public static FastRollingFileManager getFastRollingFileManager(final String fileName, final String filePattern,
            final boolean isAppend, final boolean immediateFlush, final TriggeringPolicy policy,
            final RolloverStrategy strategy, final String advertiseURI, final Layout layout) {
        return (FastRollingFileManager) getManager(fileName, new FactoryData(filePattern, isAppend, immediateFlush,
            policy, strategy, advertiseURI, layout), FACTORY);
    }

    public Boolean isEndOfBatch() {
        return isEndOfBatch.get();
    }

    public void setEndOfBatch(final boolean isEndOfBatch) {
        this.isEndOfBatch.set(Boolean.valueOf(isEndOfBatch));
    }

    @Override
    protected synchronized void write(final byte[] bytes, int offset, int length) {
        super.write(bytes, offset, length); // writes to dummy output stream

        int chunk = 0;
        do {
            if (length > buffer.remaining()) {
                flush();
            }
            chunk = Math.min(length, buffer.remaining());
            buffer.put(bytes, offset, chunk);
            offset += chunk;
            length -= chunk;
        } while (length > 0);

        if (isImmediateFlush || isEndOfBatch.get() == Boolean.TRUE) {
            flush();
        }
    }

    @Override
    protected void createFileAfterRollover() throws IOException {
        this.randomAccessFile = new RandomAccessFile(getFileName(), "rw");
        if (isAppend()) {
            randomAccessFile.seek(randomAccessFile.length());
        }
    }

    @Override
    public synchronized void flush() {
        buffer.flip();
        try {
            randomAccessFile.write(buffer.array(), 0, buffer.limit());
        } catch (final IOException ex) {
            final String msg = "Error writing to RandomAccessFile " + getName();
            throw new AppenderLoggingException(msg, ex);
        }
        buffer.clear();
    }

    @Override
    public synchronized void close() {
        flush();
        try {
            randomAccessFile.close();
        } catch (final IOException ex) {
            LOGGER.error("Unable to close RandomAccessFile " + getName() + ". "
                    + ex);
        }
    }

    /**
     * Factory to create a FastRollingFileManager.
     */
    private static class FastRollingFileManagerFactory implements ManagerFactory<FastRollingFileManager, FactoryData> {

        /**
         * Create the FastRollingFileManager.
         *
         * @param name The name of the entity to manage.
         * @param data The data required to create the entity.
         * @return a RollingFileManager.
         */
        @Override
        public FastRollingFileManager createManager(final String name, final FactoryData data) {
            final File file = new File(name);
            final File parent = file.getParentFile();
            if (null != parent && !parent.exists()) {
                parent.mkdirs();
            }

            if (!data.append) {
                file.delete();
            }
            final long size = data.append ? file.length() : 0;
            final long time = file.exists() ? file.lastModified() : System.currentTimeMillis();

            RandomAccessFile raf;
            try {
                raf = new RandomAccessFile(name, "rw");
                if (data.append) {
                    raf.seek(raf.length());
                } else {
                    raf.setLength(0);
                }
                return new FastRollingFileManager(raf, name, data.pattern, new DummyOutputStream(), data.append,
                        data.immediateFlush, size, time, data.policy, data.strategy, data.advertiseURI, data.layout);
            } catch (final IOException ex) {
                LOGGER.error("FastRollingFileManager (" + name + ") " + ex);
            }
            return null;
        }
    }

    /** {@code OutputStream} subclass that does not write anything. */
    static class DummyOutputStream extends OutputStream {
        @Override
        public void write(final int b) throws IOException {
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
        }
    }

    /**
     * Factory data.
     */
    private static class FactoryData {
        private final String pattern;
        private final boolean append;
        private final boolean immediateFlush;
        private final TriggeringPolicy policy;
        private final RolloverStrategy strategy;
        private final String advertiseURI;
        private final Layout layout;

        /**
         * Create the data for the factory.
         *
         * @param pattern The pattern.
         * @param append The append flag.
         * @param immediateFlush
         */
        public FactoryData(final String pattern, final boolean append, final boolean immediateFlush,
                           final TriggeringPolicy policy, final RolloverStrategy strategy, final String advertiseURI,
                           final Layout layout) {
            this.pattern = pattern;
            this.append = append;
            this.immediateFlush = immediateFlush;
            this.policy = policy;
            this.strategy = strategy;
            this.advertiseURI = advertiseURI;
            this.layout = layout;
        }
    }

}
