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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.batch.util.VulnerabilityUtil;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.VulnerableComponentView;
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

    private final BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

    private Date maxBomUpdatedDate;

    private final String UNDERSCORE = "_";

    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

    private final HubServices hubServices;

    private final FortifyFileTokenApi fortifyFileTokenApi;

    private final FortifyUploadApi fortifyUploadApi;

    private final PropertyConstants propertyConstants;

    public BlackDuckFortifyPushThread(final BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup, final HubServices hubServices,
            final FortifyFileTokenApi fortifyFileTokenApi, final FortifyUploadApi fortifyUploadApi, final PropertyConstants propertyConstants) {
        this.blackDuckFortifyMapperGroup = blackDuckFortifyMapperGroup;
        this.hubServices = hubServices;
        this.fortifyFileTokenApi = fortifyFileTokenApi;
        this.fortifyUploadApi = fortifyUploadApi;
        this.propertyConstants = propertyConstants;
    }

    @Override
    public Boolean call() throws DateTimeParseException, IntegrationException, IllegalArgumentException, JsonGenerationException, JsonMappingException,
            FileNotFoundException, UnsupportedEncodingException, IOException, ParseException {
        logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapperGroup.toString());
        final List<HubProjectVersion> hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();

        final SimpleDateFormat sdfr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // Get the last successful runtime of the job
        final Date getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(propertyConstants.getBatchJobStatusFilePath());
        logger.debug("Last successful job excecution:" + sdfr.format(getLastSuccessfulJobRunTime));

        // Get the project version view from Hub and calculate the max BOM updated date
        final List<ProjectVersionView> projectVersionItems = getProjectVersionItemsAndMaxBomUpdatedDate(hubProjectVersions, sdfr);
        logger.info("Compare Dates: "
                + ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)
                        || (!propertyConstants.isBatchJobStatusCheck())));
        logger.debug("maxBomUpdatedDate:: " + sdfr.format(maxBomUpdatedDate));
        logger.debug("isBatchJobStatusCheck::" + propertyConstants.isBatchJobStatusCheck());

        if ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)
                || (!propertyConstants.isBatchJobStatusCheck())) {
            // Get the vulnerabilities for all Hub project versions and merge it
            List<Vulnerability> mergedVulnerabilities = mergeVulnerabilities(hubProjectVersions, projectVersionItems);
            logger.debug("isPushForZeroVulnerability::" + propertyConstants.isPushForZeroVulnerability() + ", mergedVulnerabilities count::"
                    + mergedVulnerabilities.size());
            if (propertyConstants.isPushForZeroVulnerability() || mergedVulnerabilities.size() > 0) {
                // Removing Duplicates within multiple Hub Project Versions.
                mergedVulnerabilities = VulnerabilityUtil.removeDuplicates(mergedVulnerabilities);
                logger.debug("removed duplicates mergedVulnerabilities count::" + mergedVulnerabilities.size());
                final String fileDir = propertyConstants.getReportDir();
                final String fileName = hubProjectVersions.get(0).getHubProject() + UNDERSCORE + hubProjectVersions.get(0).getHubProjectVersion()
                        + UNDERSCORE + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

                // Write the vulnerabilities to CSV
                CSVUtils.writeToCSV(mergedVulnerabilities, fileDir + fileName, ',');

                // Get the file token for upload
                final String token = getFileToken();

                // Upload the vulnerabilities CSV to Fortify
                uploadCSV(token, fileDir + fileName, blackDuckFortifyMapperGroup.getFortifyApplicationId());

                // Delete the file token that is created for upload
                fortifyFileTokenApi.deleteFileToken();
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
     * @throws ParseException
     */
    private List<ProjectVersionView> getProjectVersionItemsAndMaxBomUpdatedDate(final List<HubProjectVersion> hubProjectVersions, final SimpleDateFormat sdfr)
            throws IllegalArgumentException, IntegrationException, ParseException {
        final List<ProjectVersionView> projectVersionItems = new ArrayList<>();
        for (final HubProjectVersion hubProjectVersion : hubProjectVersions) {
            final String projectName = hubProjectVersion.getHubProject();
            final String projectVersion = hubProjectVersion.getHubProjectVersion();

            // Get the project version
            final ProjectVersionView projectVersionItem = hubServices.getProjectVersion(projectName, projectVersion);
            projectVersionItems.add(projectVersionItem);

            // Get BOM Last updated At
            final DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Date bomUpdatedValueAt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
                    .parse(formatter.format(hubServices.getBomLastUpdatedAt(projectVersionItem)));

            if (maxBomUpdatedDate == null || bomUpdatedValueAt.after(maxBomUpdatedDate)) {
                maxBomUpdatedDate = bomUpdatedValueAt;
            }
            logger.debug("bomUpdatedValueAt::" + sdfr.format(bomUpdatedValueAt));
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
        final List<Vulnerability> mergedVulnerabilities = new ArrayList<>();

        for (final HubProjectVersion hubProjectVersion : hubProjectVersions) {

            // Get the Vulnerability information
            final List<VulnerableComponentView> vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItems.get(index));
            index++;

            // Convert the Hub Vulnerability component view to CSV Vulnerability object
            final List<Vulnerability> vulnerabilities = VulnerabilityUtil.transformMapping(hubServices, vulnerableComponentViews,
                    hubProjectVersion.getHubProject(), hubProjectVersion.getHubProjectVersion(), maxBomUpdatedDate, propertyConstants);

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
     * @throws ParseException
     */
    private Date getLastSuccessfulJobRunTime(final String fileName) throws IOException, DateTimeParseException, ParseException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse(sCurrentLine);
            }
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to find the batch_job_status.txt file", e);
        } catch (final DateTimeParseException e) {
            logger.error(e.getMessage(), e);
            throw new DateTimeParseException("Error while parsing the date. Please make sure date time format is yyyy/MM/dd HH:mm:ss.SSS", e.getParsedString(),
                    e.getErrorIndex(), e);
        } catch (final ParseException e) {
            // TODO Auto-generated catch block
            throw new ParseException("Error while parsing the date. Please make sure date time format is yyyy/MM/dd HH:mm:ss.SSS", e.getErrorOffset());
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
     * @throws IntegrationException
     */
    private String getFileToken() throws IOException, IntegrationException {
        final FileToken fileToken = new FileToken("UPLOAD");
        return fortifyFileTokenApi.getFileToken(fileToken);
    }

    /**
     * Upload the CSV to Fortify
     *
     * @param token
     * @param fileName
     * @param fortifyApplicationId
     * @throws IOException
     * @throws IntegrationException
     */
    private void uploadCSV(final String token, final String fileName, final int fortifyApplicationId) throws IOException, IntegrationException {
        final File file = new File(fileName);
        logger.debug("Uploading " + file.getName() + " to fortify");
        // Call Fortify upload
        final boolean response = fortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId, file);

        // Check if the upload is submitted successfully, if not don't delete the CSV files. It can be used for
        // debugging
        if (response) {
            if (file.exists()) {
                file.delete();
            }
            logger.info(file.getName() + " File uploaded successfully");
        }
    }

}
