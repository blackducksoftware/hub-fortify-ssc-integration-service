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
