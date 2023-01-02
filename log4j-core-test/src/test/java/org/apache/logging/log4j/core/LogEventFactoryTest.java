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
package org.apache.logging.log4j.core;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.apache.logging.log4j.core.test.junit.LoggerContextSource;
import org.apache.logging.log4j.core.test.junit.Named;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.plugins.Factory;
import org.apache.logging.log4j.plugins.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
public class LogEventFactoryTest {

    @Test
    @LoggerContextSource(value = "log4j2-config.xml", bootstrap = true)
    public void testEvent(@Named("List") final ListAppender app, final LoggerContext context) {
        final org.apache.logging.log4j.Logger logger = context.getLogger("org.apache.test.LogEventFactory");
        logger.error("error message");
        final List<LogEvent> events = app.getEvents();
        assertNotNull(events, "No events");
        assertEquals(1, events.size(), "Incorrect number of events. Expected 1, actual " + events.size());
        final LogEvent event = events.get(0);
        assertEquals("Test", event.getLoggerName(), "TestLogEventFactory wasn't used");
    }

    public static class TestLogEventFactory implements LogEventFactory {
        private final ContextDataInjector contextDataInjector;
        private final ContextDataFactory contextDataFactory;

        @Inject
        public TestLogEventFactory(final ContextDataInjector contextDataInjector,
                                   final ContextDataFactory contextDataFactory) {
            this.contextDataInjector = contextDataInjector;
            this.contextDataFactory = contextDataFactory;
        }

        @Override
        public LogEvent createEvent(final String loggerName, final Marker marker,
                                    final String fqcn, final Level level, final Message data,
                                    final List<Property> properties, final Throwable t) {
            return LogEvent.builder()
                    .setLoggerName("Test")
                    .setMarker(marker)
                    .setLoggerFqcn(fqcn)
                    .setLevel(level)
                    .setMessage(data)
                    .setContextDataInjector(contextDataInjector)
                    .setContextDataFactory(contextDataFactory)
                    .setContextData(properties)
                    .setThrown(t)
                    .build();
        }
    }

    @Factory
    public LogEventFactory logEventFactory(final TestLogEventFactory factory) {
        return factory;
    }
}
