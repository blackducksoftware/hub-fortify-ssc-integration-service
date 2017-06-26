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
package com.blackducksoftware.integration.fortify.batch.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.batch.util.VulnerabilityUtil;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class will be used as Thread and it will perform the following tasks in parallel for each Hub-Fortify mapper
 * 1) Get the Hub project version information
 * 2) Get the Maximum BOM updated date and Last successful runtime of the job
 * 3) Compare the dates, if the last BOM updated date is lesser than last successful runtime of the job, do nothing
 * else perform the following the task
 * i) Get the Vulnerabilities and merged it to single list
 * ii) Write it to CSV
 * iii) Upload the CSV to Fortify
 *
 * @author smanikantan
 *
 */
public class BlackDuckFortifyPushThread implements Callable<Boolean> {

    private BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

    private Date maxBomUpdatedDate;

    private final String UNDERSCORE = "_";

    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

    public BlackDuckFortifyPushThread(BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup) {
        this.blackDuckFortifyMapperGroup = blackDuckFortifyMapperGroup;
    }

    @Override
    public Boolean call() throws DateTimeParseException, IntegrationException, IllegalArgumentException, JsonGenerationException, JsonMappingException,
            FileNotFoundException, UnsupportedEncodingException, IOException {
        logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapperGroup.toString());
        final List<HubProjectVersion> hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();

        // Get the last successful runtime of the job
        final Date getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(PropertyConstants.getBatchJobStatusFilePath());
        logger.debug("Last successful job excecution:" + getLastSuccessfulJobRunTime);

        // Get the project version view from Hub and calculate the max BOM updated date
        final List<ProjectVersionView> projectVersionItems = getProjectVersionItemsAndMaxBomUpdatedDate(hubProjectVersions);
        logger.info("Compare Dates: "
                + ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)));
        logger.debug("getLastSuccessfulJobRunTime::" + getLastSuccessfulJobRunTime);
        logger.debug("maxBomUpdatedDate:: " + maxBomUpdatedDate);

        if ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)) {
            // Get the vulnerabilities for all Hub project versions and merge it
            List<Vulnerability> mergedVulnerabilities = mergeVulnerabilities(hubProjectVersions, projectVersionItems);
            if (mergedVulnerabilities.size() > 0) {
                if (hubProjectVersions.size() > 1) {
                    // Removing Duplicates within multiple Hub Project Versions.
                    mergedVulnerabilities = VulnerabilityUtil.removeDuplicates(mergedVulnerabilities);
                }
                final String fileDir = PropertyConstants.getReportDir();
                final String fileName = hubProjectVersions.get(0).getHubProject() + UNDERSCORE + hubProjectVersions.get(0).getHubProjectVersion()
                        + UNDERSCORE + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

                // Write the vulnerabilities to CSV
                CSVUtils.writeToCSV(mergedVulnerabilities, fileDir + fileName, ',');

                // Get the file token for upload
                String token = getFileToken();

                // Upload the vulnerabilities CSV to Fortify
                uploadCSV(token, fileDir + fileName, blackDuckFortifyMapperGroup.getFortifyApplicationId());

                // Delete the file token that is created for upload
                FortifyFileTokenApi.deleteFileToken();
            }
        }
        return true;
    }

    /**
     * Iterate the hub project versions mapper and get the project version view for each item and calculate the max BOM
     * updated date
     *
     * @param hubProjectVersions
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    private List<ProjectVersionView> getProjectVersionItemsAndMaxBomUpdatedDate(final List<HubProjectVersion> hubProjectVersions)
            throws IllegalArgumentException, IntegrationException {
        List<ProjectVersionView> projectVersionItems = new ArrayList<>();
        for (HubProjectVersion hubProjectVersion : hubProjectVersions) {
            String projectName = hubProjectVersion.getHubProject();
            String projectVersion = hubProjectVersion.getHubProjectVersion();

            // Get the project version
            final ProjectVersionView projectVersionItem = HubServices.getProjectVersion(projectName, projectVersion);
            projectVersionItems.add(projectVersionItem);
            Date bomUpdatedValueAt = HubServices.getBomLastUpdatedAt(projectVersionItem);

            if (maxBomUpdatedDate == null || bomUpdatedValueAt.after(maxBomUpdatedDate)) {
                maxBomUpdatedDate = bomUpdatedValueAt;
            }
            logger.debug("bomUpdatedValueAt::" + bomUpdatedValueAt);
        }
        return projectVersionItems;
    }

    /**
     * Iterate the hub project versions and find the vulnerabilities for Hub project version and transform the
     * vulnerability component view to CSV vulnerability view and merge all the vulnerabilities
     *
     * @param hubProjectVersions
     * @param projectVersionItems
     * @return
     * @throws IntegrationException
     * @throws IllegalArgumentException
     */
    private List<Vulnerability> mergeVulnerabilities(final List<HubProjectVersion> hubProjectVersions, final List<ProjectVersionView> projectVersionItems)
            throws IllegalArgumentException, IntegrationException {
        int index = 0;
        List<Vulnerability> mergedVulnerabilities = new ArrayList<>();
        for (HubProjectVersion hubProjectVersion : hubProjectVersions) {

            // Get the Vulnerability information
            final List<VulnerableComponentView> vulnerableComponentViews = HubServices.getVulnerabilityComponentViews(projectVersionItems.get(index));
            index++;

            // Convert the Hub Vulnerability component view to CSV Vulnerability object
            List<Vulnerability> vulnerabilities = VulnerabilityUtil.transformMapping(vulnerableComponentViews, hubProjectVersion.getHubProject(),
                    hubProjectVersion.getHubProjectVersion(), maxBomUpdatedDate);

            // Add the vulnerabilities to the main list
            mergedVulnerabilities.addAll(vulnerabilities);
        }
        return mergedVulnerabilities;
    }

    /**
     * Get the last successful job run time of the job by reading the batch_job_status.txt file
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws DateTimeParseException
     */
    private Date getLastSuccessfulJobRunTime(String fileName) throws IOException, DateTimeParseException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                final LocalDateTime localDateTime = LocalDateTime.parse(sCurrentLine, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to find the batch_job_status.txt file", e);
        } catch (DateTimeParseException e) {
            logger.error(e.getMessage(), e);
            throw new DateTimeParseException("Error while parsing the date. Please make sure date time format is yyyy/MM/dd HH:mm:ss.SSS", e.getParsedString(),
                    e.getErrorIndex(), e);
        } finally {
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
        return null;
    }

    /**
     * Get the new file token from Fortify to upload the vulnerabilities
     *
     * @return
     * @throws IOException
     */
    private String getFileToken() throws IOException {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        return FortifyFileTokenApi.getFileToken(fileToken);
    }

    /**
     * Upload the CSV to Fortify
     *
     * @param token
     * @param fileName
     * @param fortifyApplicationId
     * @throws IOException
     */
    private void uploadCSV(String token, String fileName, int fortifyApplicationId) throws IOException {
        File file = new File(fileName);
        logger.debug("Uploading " + file.getName() + " to fortify");
        // Call Fortify upload
        final JobStatusResponse uploadVulnerabilityResponseBody = FortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId, file);
        logger.debug("uploadVulnerabilityResponseBody:: " + uploadVulnerabilityResponseBody);

        // Check if the upload is submitted successfully, if not don't delete the CSV files. It can be used for
        // debugging
        if (uploadVulnerabilityResponseBody != null && "-10001".equalsIgnoreCase(uploadVulnerabilityResponseBody.getCode())
                && "Background submission succeeded.".equalsIgnoreCase(uploadVulnerabilityResponseBody.getMessage())) {
            if (file.exists()) {
                file.delete();
            }
            logger.info(file.getName() + " File uploaded successfully");
        }
    }

}
