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
package org.apache.logging.log4j.core.impl;

public class Log4jProperties {
    @Deprecated // should use DI bindings
    public static final String SHUTDOWN_CALLBACK_REGISTRY_CLASS_NAME = "log4j2.*.LoggerContext.shutdownCallbackRegistry";
    public static final String SHUTDOWN_HOOK_ENABLED = "log4j2.*.LoggerContext.shutdownHookEnabled";

    public static final String LOGGER_CONTEXT_STACKTRACE_ON_START = "log4j2.*.LoggerContext.stacktraceOnStart";

    public static final String JANSI_ENABLED = "log4j2.*.Jansi.enabled";

    @Deprecated // should use DI bindings
    public static final String CONTEXT_SELECTOR_CLASS_NAME = "log4j2.*.LoggerContext.contextSelector";

    @Deprecated // should use DI bindings
    public static final String LOG_EVENT_FACTORY_CLASS_NAME = "log4j2.*.LoggerContext.logEventFactory";

    public static final String STATUS_DEFAULT_LEVEL = "log4j2.*.StatusLogger.defaultStatusLevel";

    public static final String CONFIG_DEFAULT_LEVEL = "log4j2.*.Configuration.level";
    @Deprecated // should use DI bindings
    public static final String CONFIG_CLOCK = "log4j2.*.Configuration.clock";
    @Deprecated // should use DI bindings
    public static final String CONFIG_MERGE_STRATEGY_CLASS_NAME = "log4j2.*.Configuration.mergeStrategy";
    @Deprecated // should use DI bindings
    public static final String CONFIG_RELIABILITY_STRATEGY = "log4j2.*.Configuration.reliabilityStrategy";
    public static final String CONFIG_RELIABILITY_STRATEGY_AWAIT_UNCONDITIONALLY_MILLIS = "log4j2.*.Configuration.reliabilityStrategyAwaitUnconditionallyMillis";
    @Deprecated // should use DI bindings
    public static final String CONFIG_CONFIGURATION_FACTORY_CLASS_NAME = "log4j2.*.Configuration.factory";
    public static final String CONFIG_LOCATION = "log4j2.*.Configuration.location";
    public static final String CONFIG_V1_LOCATION = "log4j2.*.V1Compatibility.location";
    public static final String CONFIG_V1_COMPATIBILITY_ENABLED = "log4j2.*.V1Compatibility.enabled";
    public static final String CONFIG_ALLOWED_PROTOCOLS = "log4j2.*.Configuration.allowedProtocols";

    public static final String ASYNC_LOGGER_FORMAT_MESSAGES_IN_BACKGROUND = "log4j2.*.AsyncLogger.formatMsgAsync";
    public static final String ASYNC_LOGGER_QUEUE_FULL_POLICY = "log4j2.*.AsyncLogger.queueFullPolicy";
    public static final String ASYNC_LOGGER_DISCARD_THRESHOLD = "log4j2.*.AsyncLogger.discardThreshold";
    public static final String ASYNC_LOGGER_RING_BUFFER_SIZE = "log4j2.*.AsyncLogger.ringBufferSize";
    public static final String ASYNC_LOGGER_WAIT_STRATEGY = "log4j2.*.AsyncLogger.waitStrategy";
    public static final String ASYNC_LOGGER_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = "log4j2.*.AsyncLogger.synchronizeEnqueueWhenQueueFull";
    @Deprecated // should use DI bindings
    public static final String ASYNC_LOGGER_EXCEPTION_HANDLER_CLASS_NAME = "log4j2.*.AsyncLogger.exceptionHandler";
    public static final String ASYNC_LOGGER_THREAD_NAME_STRATEGY = "log4j2.*.AsyncLogger.threadNameStrategy";
    public static final String ASYNC_CONFIG_RING_BUFFER_SIZE = "log4j2.*.AsyncLoggerConfig.ringBufferSize";
    public static final String ASYNC_CONFIG_WAIT_STRATEGY = "log4j2.*.AsyncLoggerConfig.waitStrategy";
    public static final String ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = "log4j2.*.AsyncLoggerConfig.synchronizeEnqueueWhenQueueFull";
    @Deprecated // should use DI bindings
    public static final String ASYNC_CONFIG_EXCEPTION_HANDLER_CLASS_NAME = "log4j2.*.AsyncLoggerConfig.exceptionHandler";

    public static final String GC_ENABLE_DIRECT_ENCODERS = "log4j2.*.GC.enableDirectEncoders";
    public static final String GC_INITIAL_REUSABLE_MESSAGE_SIZE = "log4j2.*.GC.initialReusableMsgSize";
    public static final String GC_ENCODER_CHAR_BUFFER_SIZE = "log4j2.*.GC.encoderCharBufferSize";
    public static final String GC_ENCODER_BYTE_BUFFER_SIZE = "log4j2.*.GC.encoderByteBufferSize";
    public static final String GC_LAYOUT_STRING_BUILDER_MAX_SIZE = "log4j2.*.GC.layoutStringBuilderMaxSize";
    public static final String GC_USE_PRECISE_CLOCK = "log4j2.*.GC.usePreciseClock";

    @Deprecated // should use DI bindings
    public static final String THREAD_CONTEXT_DATA_CLASS_NAME = "log4j2.*.ThreadContext.contextData";
    @Deprecated // should use DI bindings
    public static final String THREAD_CONTEXT_DATA_INJECTOR_CLASS_NAME = "log4j2.*.ThreadContext.contextDataInjector";

    public static final String JMX_ENABLED = "log4j2.*.JMX.enabled";
    public static final String JMX_NOTIFY_ASYNC = "log4j2.*.JMX.notifyAsync";

    @Deprecated // should use DI bindings
    public static final String TRANSPORT_SECURITY_AUTHORIZATION_PROVIDER_CLASS_NAME = "log4j2.*.TransportSecurity.authorizationProvider";

    public static final String TRANSPORT_SECURITY_BASIC_USERNAME = "log4j2.*.TransportSecurity.basicUsername";
    public static final String TRANSPORT_SECURITY_BASIC_PASSWORD = "log4j2.*.TransportSecurity.basicPassword";
    @Deprecated // should use DI bindings
    public static final String TRANSPORT_SECURITY_PASSWORD_DECRYPTOR_CLASS_NAME = "log4j2.*.TransportSecurity.passwordDecryptor";

    public static final String TRANSPORT_SECURITY_TRUST_STORE_LOCATION = "log4j2.*.TransportSecurity.trustStoreLocation";
    public static final String TRANSPORT_SECURITY_TRUST_STORE_PASSWORD = "log4j2.*.TransportSecurity.trustStorePassword";
    public static final String TRANSPORT_SECURITY_TRUST_STORE_PASSWORD_FILE = "log4j2.*.TransportSecurity.trustStorePasswordFile";
    public static final String TRANSPORT_SECURITY_TRUST_STORE_PASSWORD_ENV_VAR = "log4j2.*.TransportSecurity.trustStorePasswordEnvironmentVariable";
    public static final String TRANSPORT_SECURITY_TRUST_STORE_KEY_STORE_TYPE = "log4j2.*.TransportSecurity.trustStoreKeyStoreType";
    public static final String TRANSPORT_SECURITY_TRUST_STORE_KEY_MANAGER_FACTORY_ALGORITHM = "log4j2.*.TransportSecurity.trustStoreKeyManagerFactoryAlgorithm";

    public static final String TRANSPORT_SECURITY_KEY_STORE_LOCATION = "log4j2.*.TransportSecurity.keyStoreLocation";
    public static final String TRANSPORT_SECURITY_KEY_STORE_PASSWORD = "log4j2.*.TransportSecurity.keyStorePassword";
    public static final String TRANSPORT_SECURITY_KEY_STORE_PASSWORD_FILE = "log4j2.*.TransportSecurity.keyStorePasswordFile";
    public static final String TRANSPORT_SECURITY_KEY_STORE_PASSWORD_ENV_VAR = "log4j2.*.TransportSecurity.keyStorePasswordEnvironmentVariable";
    public static final String TRANSPORT_SECURITY_KEY_STORE_TYPE = "log4j2.*.TransportSecurity.keyStoreType";
    public static final String TRANSPORT_SECURITY_KEY_STORE_KEY_MANAGER_FACTORY_ALGORITHM = "log4j2.*.TransportSecurity.keyStoreKeyManagerFactoryAlgorithm";
    public static final String TRANSPORT_SECURITY_VERIFY_HOST_NAME = "log4j2.*.TransportSecurity.verifyHostName";

    /**
     * Property that may be used to seed the UUID generation with an integer value.
     *
     * @see org.apache.logging.log4j.core.util.UuidUtil
     */
    public static final String UUID_SEQUENCE = "log4j2.*.UUID.sequence";
}