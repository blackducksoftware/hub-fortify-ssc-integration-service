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

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;

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
    private static HubServerConfig getHubServerConfig(PropertyConstants propertyConstants) {
        HubServerConfigBuilder builder = new HubServerConfigBuilder();
        builder.setHubUrl(propertyConstants.getHubServerUrl());
        builder.setUsername(propertyConstants.getHubUserName());
        builder.setPassword(propertyConstants.getHubPassword());
        builder.setTimeout(propertyConstants.getHubTimeout());

        if (propertyConstants.getHubProxyHost() != null && !"".equalsIgnoreCase(propertyConstants.getHubProxyHost())) {
            logger.info("Inside Proxy settings::" + propertyConstants.getHubProxyHost() + "::");
            builder.setProxyHost(propertyConstants.getHubProxyHost());
            builder.setProxyPort(propertyConstants.getHubProxyPort());
            builder.setProxyUsername(propertyConstants.getHubProxyUser());
            builder.setProxyPassword(propertyConstants.getHubPassword());
            builder.setIgnoredProxyHosts(propertyConstants.getHubProxyNoHost());
        }

        return builder.build();
    }

    /**
     * Get the Hub connection details from application.properties
     *
     * @return
     */
    private static CredentialsRestConnection getApplicationPropertyRestConnection(final PropertyConstants propertyConstants) {
        return getRestConnection(getHubServerConfig(propertyConstants));
    }

    /**
     * Get the Hub REST connection
     *
     * @param serverConfig
     * @return
     */
    private static CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig) {
        return getRestConnection(serverConfig, LogLevel.DEBUG);
    }

    /**
     * Get the Hub REST connection
     *
     * @param serverConfig
     * @param logLevel
     * @return
     */
    private static CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig, final LogLevel logLevel) {

        CredentialsRestConnection restConnection;
        try {
            restConnection = new CredentialsRestConnection(new PrintStreamIntLogger(System.out, logLevel),
                    serverConfig.getHubUrl(), serverConfig.getGlobalCredentials().getUsername(), serverConfig.getGlobalCredentials().getDecryptedPassword(),
                    serverConfig.getTimeout());

            restConnection.proxyHost = serverConfig.getProxyInfo().getHost();
            restConnection.proxyPort = serverConfig.getProxyInfo().getPort();
            restConnection.proxyNoHosts = serverConfig.getProxyInfo().getIgnoredProxyHosts();
            restConnection.proxyUsername = serverConfig.getProxyInfo().getUsername();
            restConnection.proxyPassword = serverConfig.getProxyInfo().getDecryptedPassword();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (EncryptionException e) {
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
    public static HubServicesFactory createHubServicesFactory(PropertyConstants propertyConstants) {
        return createHubServicesFactory(LogLevel.DEBUG, propertyConstants);
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
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
        return hubServicesFactory;
    }
}
