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
package org.apache.logging.log4j.jackson.json.template.layout.resolver;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

final class MainMapResolver implements EventResolver {

    private static final MainMapLookup MAIN_MAP_LOOKUP = new MainMapLookup();

    private final EventResolverContext context;

    private final String key;

    static String getName() {
        return "main";
    }

    MainMapResolver(final EventResolverContext context, final String key) {
        this.context = context;
        this.key = key;
    }

    @Override
    public void resolve(
            final LogEvent logEvent,
            final JsonGenerator jsonGenerator)
            throws IOException {
        final String value = MAIN_MAP_LOOKUP.lookup(key);
        final boolean valueExcluded =
                context.isBlankPropertyExclusionEnabled() &&
                        Strings.isEmpty(value);
        if (valueExcluded) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeObject(value);
        }
    }

}
