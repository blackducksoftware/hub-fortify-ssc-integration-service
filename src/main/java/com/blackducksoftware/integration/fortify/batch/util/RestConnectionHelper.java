/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.fortify.batch.util;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;
import com.blackducksoftware.integration.rest.connection.RestConnection;

/**
 * This class is used to get the Hub REST connection
 *
 * @author smanikantan
 *
 */
public final class RestConnectionHelper {

    private final static Logger logger = Logger.getLogger(RestConnectionHelper.class);

    /**
     * Build the Hub Server information for connection
     *
     * @return
     */
    private static HubServerConfig getHubServerConfig(final PropertyConstants propertyConstants) {
        final HubServerConfigBuilder builder = new HubServerConfigBuilder();
        builder.setUrl(propertyConstants.getHubServerUrl());
        builder.setUsername(propertyConstants.getHubUserName());
        builder.setPassword(propertyConstants.getHubPassword());
        builder.setApiToken(propertyConstants.getHubApiToken());
        // builder.setTrustCert(propertyConstants.isHubAlwaysTrustCert());
        builder.setTimeout(propertyConstants.getHubTimeout());

        if (propertyConstants.getHubProxyHost() != null && !"".equalsIgnoreCase(propertyConstants.getHubProxyHost())) {
            logger.info("Inside Proxy settings");
            builder.setProxyHost(propertyConstants.getHubProxyHost());
            builder.setProxyPort(propertyConstants.getHubProxyPort());
            builder.setProxyUsername(propertyConstants.getHubProxyUser());
            builder.setProxyPassword(propertyConstants.getHubProxyPassword());
            builder.setProxyNtlmDomain(propertyConstants.getHubProxyNtlmDomain());
            builder.setProxyNtlmWorkstation(propertyConstants.getHubProxyNtlmWorkstation());
            // builder.setIgnoredProxyHosts(propertyConstants.getHubProxyNoHost());
        }

        return builder.build();
    }

    /**
     * Get the Hub connection details from application.properties
     *
     * @return
     */
    private static RestConnection getApplicationPropertyRestConnection(final PropertyConstants propertyConstants) {
        return getRestConnection(getHubServerConfig(propertyConstants));
    }

    /**
     * Get the Hub REST connection
     *
     * @param serverConfig
     * @return
     */
    private static RestConnection getRestConnection(final HubServerConfig serverConfig) {
        return getRestConnection(serverConfig, LogLevel.DEBUG);
    }

    /**
     * Get the Hub REST connection
     *
     * @param serverConfig
     * @param logLevel
     * @return
     */
    private static RestConnection getRestConnection(final HubServerConfig serverConfig, final LogLevel logLevel) {

        final RestConnection restConnection;
        try {
            restConnection = serverConfig.createRestConnection(new PrintStreamIntLogger(System.out, logLevel));
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (final EncryptionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return restConnection;
    }

    /**
     * Create the Hub Services factory
     *
     * @return
     */
    public static HubServicesFactory createHubServicesFactory(final PropertyConstants propertyConstants) {
        switch (propertyConstants.getLogLevel()) {
        case "DEBUG":
            return createHubServicesFactory(LogLevel.DEBUG, propertyConstants);
        case "ERROR":
            return createHubServicesFactory(LogLevel.ERROR, propertyConstants);
        case "WARN":
            return createHubServicesFactory(LogLevel.WARN, propertyConstants);
        case "TRACE":
            return createHubServicesFactory(LogLevel.TRACE, propertyConstants);
        default:
            return createHubServicesFactory(LogLevel.INFO, propertyConstants);
        }

    }

    /**
     * Create the Hub Services factory based on loglevel
     *
     * @param logLevel
     * @return
     */
    private static HubServicesFactory createHubServicesFactory(final LogLevel logLevel, final PropertyConstants propertyConstants) {
        return createHubServicesFactory(new PrintStreamIntLogger(System.out, logLevel), propertyConstants);
    }

    /**
     * Create the Hub Services factory based on logger
     *
     * @param logger
     * @return
     */
    private static HubServicesFactory createHubServicesFactory(final IntLogger logger, final PropertyConstants propertyConstants) {
        final RestConnection restConnection = getApplicationPropertyRestConnection(propertyConstants);
        restConnection.logger = logger;
        // Adjust the number of connections in the connection pool. The keepAlive info is the same as the default
        // constructor
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(5, TimeUnit.MINUTES);
        connManager.setDefaultMaxPerRoute(propertyConstants.getMaximumThreadSize());
        connManager.setMaxTotal(propertyConstants.getMaximumThreadSize());
        restConnection.getClientBuilder().setConnectionManager(connManager).setConnectionManagerShared(true);

        // restConnection.getClientBuilder().setMaxConnPerRoute(propertyConstants.getMaximumThreadSize())
        // .setMaxConnTotal(propertyConstants.getMaximumThreadSize())
        // .setConnectionTimeToLive(5, TimeUnit.MINUTES);
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
        return hubServicesFactory;
    }
}
