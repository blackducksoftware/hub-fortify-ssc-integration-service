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

public class RestConnectionHelper {

    private static HubServerConfig getHubServerConfig() {
        HubServerConfigBuilder builder = new HubServerConfigBuilder();
        builder.setHubUrl(PropertyConstants.getProperty("hub.server.url"));
        builder.setUsername(PropertyConstants.getProperty("hub.username"));
        builder.setPassword(PropertyConstants.getProperty("hub.password"));
        builder.setTimeout(PropertyConstants.getProperty("hub.timeout"));

        return builder.build();
    }

    private static CredentialsRestConnection getApplicationPropertyRestConnection() {
        return getRestConnection(getHubServerConfig());
    }

    private static CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig) {
        return getRestConnection(serverConfig, LogLevel.TRACE);
    }

    private static CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig, final LogLevel logLevel) {

        CredentialsRestConnection restConnection;
        try {
            restConnection = new CredentialsRestConnection(new PrintStreamIntLogger(System.out, logLevel),
                    serverConfig.getHubUrl(), serverConfig.getGlobalCredentials().getUsername(), serverConfig.getGlobalCredentials().getDecryptedPassword(),
                    serverConfig.getTimeout());
        } catch (EncryptionException e1) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e1);
        }
        restConnection.proxyHost = serverConfig.getProxyInfo().getHost();
        restConnection.proxyPort = serverConfig.getProxyInfo().getPort();
        restConnection.proxyNoHosts = serverConfig.getProxyInfo().getIgnoredProxyHosts();
        restConnection.proxyUsername = serverConfig.getProxyInfo().getUsername();
        try {
            restConnection.proxyPassword = serverConfig.getProxyInfo().getDecryptedPassword();
        } catch (IllegalArgumentException | EncryptionException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

        return restConnection;
    }

    public static HubServicesFactory createHubServicesFactory() {
        return createHubServicesFactory(LogLevel.TRACE);
    }

    private static HubServicesFactory createHubServicesFactory(final LogLevel logLevel) {
        return createHubServicesFactory(new PrintStreamIntLogger(System.out, logLevel));
    }

    private static HubServicesFactory createHubServicesFactory(final IntLogger logger) {
        final RestConnection restConnection = getApplicationPropertyRestConnection();
        restConnection.logger = logger;
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
        return hubServicesFactory;
    }
}
