/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.fortify.batch.util;

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

    /**
     * Build the Hub Server information for connection
     *
     * @return
     */
    private static HubServerConfig getHubServerConfig() {
        HubServerConfigBuilder builder = new HubServerConfigBuilder();
        builder.setHubUrl(PropertyConstants.getHubServerUrl());
        builder.setUsername(PropertyConstants.getHubUserName());
        builder.setPassword(PropertyConstants.getHubPassword());
        builder.setTimeout(PropertyConstants.getHubTimeout());

        return builder.build();
    }

    /**
     * Get the Hub connection details from application.properties
     *
     * @return
     */
    private static CredentialsRestConnection getApplicationPropertyRestConnection() {
        return getRestConnection(getHubServerConfig());
    }

    /**
     * Get the Hub REST connection
     *
     * @param serverConfig
     * @return
     */
    private static CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig) {
        return getRestConnection(serverConfig, LogLevel.TRACE);
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
            throw new RuntimeException(e);
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }

        return restConnection;
    }

    /**
     * Create the Hub Services factory
     *
     * @return
     */
    public static HubServicesFactory createHubServicesFactory() {
        return createHubServicesFactory(LogLevel.DEBUG);
    }

    /**
     * Create the Hub Services factory based on loglevel
     *
     * @param logLevel
     * @return
     */
    private static HubServicesFactory createHubServicesFactory(final LogLevel logLevel) {
        return createHubServicesFactory(new PrintStreamIntLogger(System.out, logLevel));
    }

    /**
     * Create the Hub Services factory based on logger
     * 
     * @param logger
     * @return
     */
    private static HubServicesFactory createHubServicesFactory(final IntLogger logger) {
        final RestConnection restConnection = getApplicationPropertyRestConnection();
        restConnection.logger = logger;
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
        return hubServicesFactory;
    }
}
