/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.core.impl;

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.ScopedContext;
import org.apache.logging.log4j.core.util.ContextDataProvider;

/**
 * ContextDataProvider for {@code Map<String, String>} data.
 * @since 2.24.0
 */
@ServiceProvider(value = ContextDataProvider.class, resolution = Resolution.OPTIONAL)
public class ScopedContextDataProvider implements ContextDataProvider {

    @Override
    public String get(String key) {
        return ScopedContext.getString(key);
    }

    @Override
    public Map<String, String> supplyContextData() {
        Map<String, Object> contextMap = ScopedContext.getContextMap();
        if (!contextMap.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            contextMap.forEach((key, value) -> map.put(key, value.toString()));
            return map;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public int size() {
        return ScopedContext.size();
    }

    @Override
    public void addAll(Map<String, String> map) {
        ScopedContext.addAll(map);
    }
}
