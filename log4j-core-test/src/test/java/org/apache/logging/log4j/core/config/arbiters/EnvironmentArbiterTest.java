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
package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests system property condition processing.
 */
public class EnvironmentArbiterTest {

    static final String CONFIG = "log4j2-environmentArbiters.xml";
    static LoggerContext loggerContext = null;

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @AfterEach
    public void after() {
        loggerContext.stop();
        loggerContext = null;
    }

    @Test
    public void prodTest() {
        env.set("ENV", "prod");
        loggerContext = Configurator.initialize(null, CONFIG);
        assertNotNull(loggerContext);
        Appender app = loggerContext.getConfiguration().getAppender("Out");
        assertNotNull(app);
        assertTrue(app instanceof ListAppender);
    }

    @Test
    public void devTest() {
        env.set("ENV", "dev");
        loggerContext = Configurator.initialize(null, CONFIG);
        assertNotNull(loggerContext);
        Appender app = loggerContext.getConfiguration().getAppender("Out");
        assertNotNull(app);
        assertTrue(app instanceof ConsoleAppender);
    }
}
