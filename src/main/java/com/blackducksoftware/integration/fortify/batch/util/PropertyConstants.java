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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class is to read the application properties key-value pairs
 *
 * @author smanikantan
 *
 */
@Configuration
public class PropertyConstants {

    private String hubUserName;

    @Value("${hub.username}")
    public void setHubUserName(final String hubUserName) {
        this.hubUserName = hubUserName;
    }

    private String hubPassword;

    @Value("${hub.password}")
    public void setHubPassword(final String hubPassword) {
        this.hubPassword = hubPassword;
    }

    private String hubApiToken;

    @Value("${hub.api.token}")
    public void setHubApiToken(final String hubApiToken) {
        this.hubApiToken = hubApiToken;
    }

    private boolean hubAlwaysTrustCert;

    @Value("${hub.always.trust.cert}")
    public void setHubAlwaysTrustCert(final boolean hubAlwaysTrustCert) {
        this.hubAlwaysTrustCert = hubAlwaysTrustCert;
    }

    private String hubTimeout;

    @Value("${hub.timeout}")
    public void setHubTimeout(final String hubTimeout) {
        this.hubTimeout = hubTimeout;
    }

    private String hubServerUrl;

    @Value("${hub.server.url}")
    public void setHubServerUrl(final String hubServerUrl) {
        this.hubServerUrl = hubServerUrl;
    }

    private String hubProxyHost;

    @Value("${hub.proxy.host}")
    public void setHubProxyHost(final String hubProxyHost) {
        this.hubProxyHost = hubProxyHost;
    }

    private String hubProxyPort;

    @Value("${hub.proxy.port}")
    public void setHubProxyPort(final String hubProxyPort) {
        this.hubProxyPort = hubProxyPort;
    }

    private String hubProxyUser;

    @Value("${hub.proxy.user}")
    public void setHubProxyUser(final String hubProxyUser) {
        this.hubProxyUser = hubProxyUser;
    }

    private String hubProxyPassword;

    @Value("${hub.proxy.password}")
    public void setHubProxyPassword(final String hubProxyPassword) {
        this.hubProxyPassword = hubProxyPassword;
    }

    private String hubProxyNtlmDomain;

    @Value("${hub.proxy.Ntlm.Domain}")
    public void setHubProxyNtlmDomain(final String hubProxyNtlmDomain) {
        this.hubProxyNtlmDomain = hubProxyNtlmDomain;
    }

    private String hubProxyNtlmWorkstation;

    @Value("${hub.proxy.Ntlm.Workstation}")
    public void setHubProxyNtlmWorkstation(final String hubProxyNtlmWorkstation) {
        this.hubProxyNtlmWorkstation = hubProxyNtlmWorkstation;
    }

    private String hubProxyNoHost;

    @Value("${hub.proxy.nohost}")
    public void setHubProxyNoHost(final String hubProxyNoHost) {
        this.hubProxyNoHost = hubProxyNoHost;
    }

    private String fortifyUserName;

    @Value("${fortify.username}")
    public void setFortifyUserName(final String fortifyUserName) {
        this.fortifyUserName = fortifyUserName;
    }

    private String fortifyPassword;

    @Value("${fortify.password}")
    public void setFortifyPassword(final String fortifyPassword) {
        this.fortifyPassword = fortifyPassword;
    }

    private String fortifyServerUrl;

    @Value("${fortify.server.url}")
    public void setFortifyServerUrl(final String fortifyServerUrl) {
        this.fortifyServerUrl = fortifyServerUrl;
    }

    private String batchJobStatusFilePath;

    @Value("${hub.fortify.batch.job.status.file.path}")
    public void setBatchJobStatusFilePath(final String batchJobStatusFilePath) {
        this.batchJobStatusFilePath = batchJobStatusFilePath;
    }

    private String reportDir;

    @Value("${hub.fortify.report.dir}")
    public void setReportDir(final String reportDir) {
        this.reportDir = reportDir;
    }

    private String mappingJsonPath;

    @Value("${hub.fortify.mapping.file.path}")
    public void setMappingJsonPath(final String mappingJsonPath) {
        this.mappingJsonPath = mappingJsonPath;
    }

    private String attributeFilePath;

    @Value("${attribute.file}")
    public void setAttributeFilePath(final String attributeFilePath) {
        this.attributeFilePath = attributeFilePath;
    }

    private int maximumThreadSize;

    @Value("${maximum.thread.size}")
    public void setMaximumThreadSize(final int maximumThreadSize) {
        this.maximumThreadSize = maximumThreadSize;
    }

    private boolean batchJobStatusCheck;

    @Value("${batch.job.status.check}")
    public void setBatchJobStatusCheck(final boolean batchJobStatusCheck) {
        this.batchJobStatusCheck = batchJobStatusCheck;
    }

    private String pluginVersion;

    @Value("${plugin.version}")
    public void setPluginVersion(final String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    private String logLevel;

    @Value("${logging.level.com.blackducksoftware}")
    public void setLogLevel(final String logLevel) {
        this.logLevel = logLevel;
    }

    public String getHubUserName() {
        return hubUserName;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public String getHubApiToken() {
        return hubApiToken;
    }

    public boolean isHubAlwaysTrustCert() {
        return hubAlwaysTrustCert;
    }

    public String getHubTimeout() {
        return hubTimeout;
    }

    public String getHubServerUrl() {
        return hubServerUrl;
    }

    public String getHubProxyHost() {
        return hubProxyHost;
    }

    public String getHubProxyPort() {
        return hubProxyPort;
    }

    public String getHubProxyUser() {
        return hubProxyUser;
    }

    public String getHubProxyPassword() {
        return hubProxyPassword;
    }

    public String getHubProxyNtlmDomain() {
        return hubProxyNtlmDomain;
    }

    public String getHubProxyNtlmWorkstation() {
        return hubProxyNtlmWorkstation;
    }

    public String getHubProxyNoHost() {
        return hubProxyNoHost;
    }

    public String getFortifyUserName() {
        return fortifyUserName;
    }

    public String getFortifyPassword() {
        return fortifyPassword;
    }

    public String getFortifyServerUrl() {
        return fortifyServerUrl;
    }

    public String getBatchJobStatusFilePath() {
        return batchJobStatusFilePath;
    }

    public String getReportDir() {
        return reportDir;
    }

    public String getMappingJsonPath() {
        return mappingJsonPath;
    }

    public String getAttributeFilePath() {
        return attributeFilePath;
    }

    public int getMaximumThreadSize() {
        return maximumThreadSize;
    }

    public boolean isBatchJobStatusCheck() {
        return batchJobStatusCheck;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public String getLogLevel() {
        return logLevel;
    }
}
