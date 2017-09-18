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
    public void setHubUserName(String hubUserName) {
        this.hubUserName = hubUserName;
    }

    private String hubPassword;

    @Value("${hub.password}")
    public void setHubPassword(String hubPassword) {
        this.hubPassword = hubPassword;
    }

    private String hubTimeout;

    @Value("${hub.timeout}")
    public void setHubTimeout(String hubTimeout) {
        this.hubTimeout = hubTimeout;
    }

    private String hubServerUrl;

    @Value("${hub.server.url}")
    public void setHubServerUrl(String hubServerUrl) {
        this.hubServerUrl = hubServerUrl;
    }

    private String hubProxyHost;

    @Value("${hub.proxy.host}")
    public void setHubProxyHost(String hubProxyHost) {
        this.hubProxyHost = hubProxyHost;
    }

    private String hubProxyPort;

    @Value("${hub.proxy.port}")
    public void setHubProxyPort(String hubProxyPort) {
        this.hubProxyPort = hubProxyPort;
    }

    private String hubProxyUser;

    @Value("${hub.proxy.user}")
    public void setHubProxyUser(String hubProxyUser) {
        this.hubProxyUser = hubProxyUser;
    }

    private String hubProxyPassword;

    @Value("${hub.proxy.password}")
    public void setHubProxyPassword(String hubProxyPassword) {
        this.hubProxyPassword = hubProxyPassword;
    }

    private String hubProxyNoHost;

    @Value("${hub.proxy.nohost}")
    public void setHubProxyNoHost(String hubProxyNoHost) {
        this.hubProxyNoHost = hubProxyNoHost;
    }

    private String fortifyUserName;

    @Value("${fortify.username}")
    public void setFortifyUserName(String fortifyUserName) {
        this.fortifyUserName = fortifyUserName;
    }

    private String fortifyPassword;

    @Value("${fortify.password}")
    public void setFortifyPassword(String fortifyPassword) {
        this.fortifyPassword = fortifyPassword;
    }

    private String fortifyServerUrl;

    @Value("${fortify.server.url}")
    public void setFortifyServerUrl(String fortifyServerUrl) {
        this.fortifyServerUrl = fortifyServerUrl;
    }

    private String batchJobStatusFilePath;

    @Value("${hub.fortify.batch.job.status.file.path}")
    public void setBatchJobStatusFilePath(String batchJobStatusFilePath) {
        this.batchJobStatusFilePath = batchJobStatusFilePath;
    }

    private String reportDir;

    @Value("${hub.fortify.report.dir}")
    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    private String mappingJsonPath;

    @Value("${hub.fortify.mapping.file.path}")
    public void setMappingJsonPath(String mappingJsonPath) {
        this.mappingJsonPath = mappingJsonPath;
    }

    private String attributeFilePath;

    @Value("${attribute.file}")
    public void setAttributeFilePath(String attributeFilePath) {
        this.attributeFilePath = attributeFilePath;
    }

    private int maximumThreadSize;

    @Value("${maximum.thread.size}")
    public void setMaximumThreadSize(int maximumThreadSize) {
        this.maximumThreadSize = maximumThreadSize;
    }

    private boolean batchJobStatusCheck;

    @Value("${batch.job.status.check}")
    public void setBatchJobStatusCheck(boolean batchJobStatusCheck) {
        this.batchJobStatusCheck = batchJobStatusCheck;
    }

    private String pluginVersion;

    @Value("${plugin.version}")
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getHubUserName() {
        return hubUserName;
    }

    public String getHubPassword() {
        return hubPassword;
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
}
