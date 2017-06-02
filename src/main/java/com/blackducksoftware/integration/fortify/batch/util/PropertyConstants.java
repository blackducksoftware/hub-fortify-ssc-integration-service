/**
 * Copyright (C) 2017 Black Duck Software, Inc.
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

    private static String hubUserName;

    @Value("${hub.username}")
    public void setHubUserName(String hubUserName) {
        PropertyConstants.hubUserName = hubUserName;
    }

    private static String hubPassword;

    @Value("${hub.password}")
    public void setHubPassword(String hubPassword) {
        PropertyConstants.hubPassword = hubPassword;
    }

    private static String hubTimeout;

    @Value("${hub.timeout}")
    public void setHubTimeout(String hubTimeout) {
        PropertyConstants.hubTimeout = hubTimeout;
    }

    private static String hubServerUrl;

    @Value("${hub.server.url}")
    public void setHubServerUrl(String hubServerUrl) {
        PropertyConstants.hubServerUrl = hubServerUrl;
    }

    private static String hubProxyHost;

    @Value("${hub.proxy.host}")
    public void setHubProxyHost(String hubProxyHost) {
        PropertyConstants.hubProxyHost = hubProxyHost;
    }

    private static String hubProxyPort;

    @Value("${hub.proxy.port}")
    public void setHubProxyPort(String hubProxyPort) {
        PropertyConstants.hubProxyPort = hubProxyPort;
    }

    private static String hubProxyUser;

    @Value("${hub.proxy.user}")
    public void setHubProxyUser(String hubProxyUser) {
        PropertyConstants.hubProxyUser = hubProxyUser;
    }

    private static String hubProxyPassword;

    @Value("${hub.proxy.password}")
    public void setHubProxyPassword(String hubProxyPassword) {
        PropertyConstants.hubProxyPassword = hubProxyPassword;
    }

    private static String hubProxyNoHost;

    @Value("${hub.proxy.nohost}")
    public void setHubProxyNoHost(String hubProxyNoHost) {
        PropertyConstants.hubProxyNoHost = hubProxyNoHost;
    }

    private static String fortifyUserName;

    @Value("${fortify.username}")
    public void setFortifyUserName(String fortifyUserName) {
        PropertyConstants.fortifyUserName = fortifyUserName;
    }

    private static String fortifyPassword;

    @Value("${fortify.password}")
    public void setFortifyPassword(String fortifyPassword) {
        PropertyConstants.fortifyPassword = fortifyPassword;
    }

    private static String fortifyServerUrl;

    @Value("${fortify.server.url}")
    public void setFortifyServerUrl(String fortifyServerUrl) {
        PropertyConstants.fortifyServerUrl = fortifyServerUrl;
    }

    private static String batchJobStatusFilePath;

    @Value("${hub.fortify.batch.job.status.file.path}")
    public void setBatchJobStatusFilePath(String batchJobStatusFilePath) {
        PropertyConstants.batchJobStatusFilePath = batchJobStatusFilePath;
    }

    private static String reportDir;

    @Value("${hub.fortify.report.dir}")
    public void setReportDir(String reportDir) {
        PropertyConstants.reportDir = reportDir;
    }

    private static String mappingJsonPath;

    @Value("${hub.fortify.mapping.file.path}")
    public void setMappingJsonPath(String mappingJsonPath) {
        PropertyConstants.mappingJsonPath = mappingJsonPath;
    }

    private static int maximumThreadSize;

    @Value("${maximum.thread.size}")
    public void setMaximumThreadSize(int maximumThreadSize) {
        PropertyConstants.maximumThreadSize = maximumThreadSize;
    }

    public static String getHubUserName() {
        return hubUserName;
    }

    public static String getHubPassword() {
        return hubPassword;
    }

    public static String getHubTimeout() {
        return hubTimeout;
    }

    public static String getHubServerUrl() {
        return hubServerUrl;
    }

    public static String getHubProxyHost() {
        return hubProxyHost;
    }

    public static String getHubProxyPort() {
        return hubProxyPort;
    }

    public static String getHubProxyUser() {
        return hubProxyUser;
    }

    public static String getHubProxyPassword() {
        return hubProxyPassword;
    }

    public static String getHubProxyNoHost() {
        return hubProxyNoHost;
    }

    public static String getFortifyUserName() {
        return fortifyUserName;
    }

    public static String getFortifyPassword() {
        return fortifyPassword;
    }

    public static String getFortifyServerUrl() {
        return fortifyServerUrl;
    }

    public static String getBatchJobStatusFilePath() {
        return batchJobStatusFilePath;
    }

    public static String getReportDir() {
        return reportDir;
    }

    public static String getMappingJsonPath() {
        return mappingJsonPath;
    }

    public static int getMaximumThreadSize() {
        return maximumThreadSize;
    }
}
