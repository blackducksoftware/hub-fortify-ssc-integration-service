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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;

@Configuration
@ConfigurationProperties(value = "classpath:application.properties")
public class RestConnectionHelper {
    @Autowired
    private Environment env;

    public String getProperty(final String key) {
        return env.getProperty(key);
    }

    public HubServerConfig getHubServerConfig() {
        HubServerConfigBuilder builder = new HubServerConfigBuilder();
        builder.setHubUrl(env.getProperty("hub.server.url"));
        builder.setUsername(env.getProperty("hub.username"));
        builder.setPassword(env.getProperty("hub.password"));
        builder.setTimeout(env.getProperty("hub.timeout"));

        return builder.build();
    }

    public String getIntegrationHubServerUrl() {
        return env.getProperty("hub.server.url");
    }

    public String getUsername() {
        return env.getProperty("hub.username");
    }

    public String getPassword() {
        return env.getProperty("hub.password");
    }

    public CredentialsRestConnection getApplicationPropertyRestConnection() throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return getRestConnection(getHubServerConfig());
    }

    public CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig)
            throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return getRestConnection(serverConfig, LogLevel.TRACE);
    }

    public CredentialsRestConnection getRestConnection(final HubServerConfig serverConfig, final LogLevel logLevel)
            throws IllegalArgumentException, EncryptionException, HubIntegrationException {

        final CredentialsRestConnection restConnection = new CredentialsRestConnection(new PrintStreamIntLogger(System.out, logLevel),
                serverConfig.getHubUrl(), serverConfig.getGlobalCredentials().getUsername(), serverConfig.getGlobalCredentials().getDecryptedPassword(),
                serverConfig.getTimeout());
        restConnection.proxyHost = serverConfig.getProxyInfo().getHost();
        restConnection.proxyPort = serverConfig.getProxyInfo().getPort();
        restConnection.proxyNoHosts = serverConfig.getProxyInfo().getIgnoredProxyHosts();
        restConnection.proxyUsername = serverConfig.getProxyInfo().getUsername();
        restConnection.proxyPassword = serverConfig.getProxyInfo().getDecryptedPassword();

        return restConnection;
    }

    public HubServicesFactory createHubServicesFactory() throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return createHubServicesFactory(LogLevel.TRACE);
    }

    public HubServicesFactory createHubServicesFactory(final LogLevel logLevel) throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return createHubServicesFactory(new PrintStreamIntLogger(System.out, logLevel));
    }

    public HubServicesFactory createHubServicesFactory(final IntLogger logger) throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        final RestConnection restConnection = getApplicationPropertyRestConnection();
        restConnection.logger = logger;
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
        return hubServicesFactory;
    }
}
