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
package org.apache.logging.log4j.core.lookup;

import java.io.File;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationAware;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.logging.log4j.core.lookup.Log4jLookup.KEY_CONFIG_LOCATION;
import static org.apache.logging.log4j.core.lookup.Log4jLookup.KEY_CONFIG_PARENT_LOCATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class Log4jLookupTest {

    private final static File EXPECT = new File(System.getProperty("user.home"), "/a/b/c/d/e/log4j2.xml");

    @Mock
    private LoggerContext mockCtx;
    @Mock
    private Configuration config;
    @Mock
    private ConfigurationSource configSrc;

    @BeforeEach
    public void setup() {
        ContextAnchor.THREAD_CONTEXT.set(mockCtx);
        given(config.getConfigurationSource()).willReturn(configSrc);
        given(configSrc.getFile()).willReturn(EXPECT);
    }

    @AfterEach
    public void cleanup() {
        ContextAnchor.THREAD_CONTEXT.set(null);
    }

    @Test
    public void lookupConfigLocation() {
        final StrLookup log4jLookup = new Log4jLookup();
        ((ConfigurationAware) log4jLookup).setConfiguration(config);
        final String value = log4jLookup.lookup(KEY_CONFIG_LOCATION);
        assertEquals(EXPECT.getAbsolutePath(), value);
    }

    @Test
    public void lookupConfigParentLocation() {
        final StrLookup log4jLookup = new Log4jLookup();
        ((ConfigurationAware) log4jLookup).setConfiguration(config);
        final String value = log4jLookup.lookup(KEY_CONFIG_PARENT_LOCATION);
        assertEquals(EXPECT.getParentFile().getAbsolutePath(), value);
    }
}
