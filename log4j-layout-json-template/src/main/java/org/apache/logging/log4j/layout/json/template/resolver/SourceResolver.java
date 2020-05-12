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
package org.apache.logging.log4j.layout.json.template.resolver;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.json.template.util.JsonWriter;

final class SourceResolver implements EventResolver {

    private static final EventResolver NULL_RESOLVER =
            (final LogEvent value, final JsonWriter jsonWriter) ->
                    jsonWriter.writeNull();

    private static final EventResolver CLASS_NAME_RESOLVER =
            (final LogEvent logEvent, final JsonWriter jsonWriter) -> {
                final StackTraceElement logEventSource = logEvent.getSource();
                if (logEventSource == null) {
                    jsonWriter.writeNull();
                } else {
                    final String sourceClassName = logEventSource.getClassName();
                    jsonWriter.writeString(sourceClassName);
                }
            };

    private static final EventResolver FILE_NAME_RESOLVER =
            (final LogEvent logEvent, final JsonWriter jsonWriter) -> {
                final StackTraceElement logEventSource = logEvent.getSource();
                if (logEventSource == null) {
                    jsonWriter.writeNull();
                } else {
                    final String sourceFileName = logEventSource.getFileName();
                    jsonWriter.writeString(sourceFileName);
                }
            };

    private static final EventResolver LINE_NUMBER_RESOLVER =
            (final LogEvent logEvent, final JsonWriter jsonWriter) -> {
                final StackTraceElement logEventSource = logEvent.getSource();
                if (logEventSource == null) {
                    jsonWriter.writeNull();
                } else {
                    final int sourceLineNumber = logEventSource.getLineNumber();
                    jsonWriter.writeNumber(sourceLineNumber);
                }
            };

    private static final EventResolver METHOD_NAME_RESOLVER =
            (final LogEvent logEvent, final JsonWriter jsonWriter) -> {
                final StackTraceElement logEventSource = logEvent.getSource();
                if (logEventSource == null) {
                    jsonWriter.writeNull();
                } else {
                    final String sourceMethodName = logEventSource.getMethodName();
                    jsonWriter.writeString(sourceMethodName);
                }
            };

    private final boolean locationInfoEnabled;

    private final EventResolver internalResolver;

    SourceResolver(final EventResolverContext context, final String key) {
        this.locationInfoEnabled = context.isLocationInfoEnabled();
        this.internalResolver = createInternalResolver(context, key);
    }

    private static EventResolver createInternalResolver(
            final EventResolverContext context,
            final String key) {
        if (!context.isLocationInfoEnabled()) {
            return NULL_RESOLVER;
        }
        switch (key) {
            case "className": return CLASS_NAME_RESOLVER;
            case "fileName": return FILE_NAME_RESOLVER;
            case "lineNumber": return LINE_NUMBER_RESOLVER;
            case "methodName": return METHOD_NAME_RESOLVER;
        }
        throw new IllegalArgumentException("unknown key: " + key);
    }

    static String getName() {
        return "source";
    }

    @Override
    public boolean isResolvable(final LogEvent logEvent) {
        return locationInfoEnabled && logEvent.getSource() != null;
    }

    @Override
    public void resolve(
            final LogEvent logEvent,
            final JsonWriter jsonWriter) {
        internalResolver.resolve(logEvent, jsonWriter);
    }

}
