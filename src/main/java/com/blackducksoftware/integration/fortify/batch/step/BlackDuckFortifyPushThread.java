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
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.BlackDuckUtil;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

/**
 * This class will be used as Thread and it will perform the following tasks in parallel for each Hub-Fortify mapper
 * 1) Get the Hub project version information
 * 2) Get the Last BOM updated date and Last successful runtime of the job
 * 3) Compare the dates, if the last BOM updated date is lesser than last successful runtime of the job, do nothing
 * else perform the following the task
 * i) Get the Vulnerabilities
 * ii) Write it to CSV
 * iii) Upload the CSV to Fortify
 *
 * @author smanikantan
 *
 */
public class BlackDuckFortifyPushThread implements Runnable {

    private BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

    private Date maxBomUpdatedDate;

    private final String UNDERSCORE = "_";

    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

    private List<Vulnerability> transformMapping(List<VulnerableComponentView> vulnerabilityComponentViews, String hubProjectName,
            String hubProjectVersion) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        vulnerabilityComponentViews.forEach(vulnerableComponentView -> {
            Vulnerability vulnerability = new Vulnerability();
            vulnerability.setProjectName(String.valueOf(hubProjectName));
            vulnerability.setProjectVersion(String.valueOf(hubProjectVersion));
            String[] componentVersionLinkArr = vulnerableComponentView.getComponentVersionLink().split("/");
            vulnerability.setProjectId(String.valueOf(componentVersionLinkArr[5]));
            vulnerability.setVersionId(String.valueOf(componentVersionLinkArr[7]));
            vulnerability.setChannelVersionId("");
            vulnerability.setComponentName(String.valueOf(vulnerableComponentView.getComponentName()));
            vulnerability.setVersion(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setChannelVersionOrigin(String.valueOf(vulnerableComponentView.getComponentVersionOriginName()));
            vulnerability.setChannelVersionOriginId(String.valueOf(vulnerableComponentView.getComponentVersionOriginId()));
            vulnerability.setChannelVersionOriginName(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setVulnerabilityId(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()));
            vulnerability.setDescription(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getDescription().replaceAll("\\r\\n", "")));
            vulnerability.setPublishedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityPublishedDate());
            vulnerability.setUpdatedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityUpdatedDate());
            vulnerability.setBaseScore(vulnerableComponentView.getVulnerabilityWithRemediation().getBaseScore());
            vulnerability.setExploitability(vulnerableComponentView.getVulnerabilityWithRemediation().getExploitabilitySubscore());
            vulnerability.setImpact(vulnerableComponentView.getVulnerabilityWithRemediation().getImpactSubscore());
            vulnerability.setVulnerabilitySource(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSource()));
            vulnerability.setHubVulnerabilityUrl(PropertyConstants.getHubServerUrl() + "/ui/vulnerabilities/id:"
                    + String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()) + "/view:overview");
            vulnerability.setRemediationStatus(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationStatus()));
            vulnerability.setRemediationTargetDate(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationTargetAt());
            vulnerability.setRemediationActualDate(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationActualAt());
            vulnerability.setRemediationComment(String.valueOf(""));
            vulnerability.setUrl("NVD".equalsIgnoreCase(vulnerableComponentView.getVulnerabilityWithRemediation().getSource())
                    ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()
                    : "");
            vulnerability.setSeverity(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSeverity()));
            vulnerability.setScanDate(maxBomUpdatedDate);
            vulnerabilities.add(vulnerability);
        });
        return vulnerabilities;
    }

    public BlackDuckFortifyPushThread(BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup) {
        this.blackDuckFortifyMapperGroup = blackDuckFortifyMapperGroup;
    }

    @Override
    public void run() {
        if (blackDuckFortifyMapperGroup != null) {
            logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapperGroup.toString());

            ProjectVersionView projectVersionItem = null;
            List<VulnerableComponentView> vulnerableComponentViews;
            List<HubProjectVersion> hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();
            List<ProjectVersionView> projectVersionItems = new ArrayList<>();
            List<Vulnerability> mergedVulnerabilities = new ArrayList<>();

            // Get the last successful runtime of the job
            Date getLastSuccessfulJobRunTime;
            try {
                getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(PropertyConstants.getBatchJobStatusFilePath());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
            logger.info("Last successful job excecution:" + getLastSuccessfulJobRunTime);

            for (HubProjectVersion hubProjectVersion : hubProjectVersions) {
                String projectName = hubProjectVersion.getHubProject();
                String projectVersion = hubProjectVersion.getHubProjectVersion();

                // Get the project version
                try {
                    projectVersionItem = HubServices.getProjectVersion(projectName, projectVersion);
                    projectVersionItems.add(projectVersionItem);
                    Date bomUpdatedValueAt = HubServices.getBomLastUpdatedAt(projectVersionItem);

                    if (maxBomUpdatedDate == null || bomUpdatedValueAt.after(maxBomUpdatedDate)) {
                        maxBomUpdatedDate = bomUpdatedValueAt;
                    }
                    logger.info("maxBomUpdatedDate::" + maxBomUpdatedDate);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error while getting the Hub project version info", e);
                } catch (IntegrationException e) {
                    throw new RuntimeException("Error while getting the Hub project version info", e);
                }
            }
            logger.info("Compare Dates: " + ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime))
                    || (getLastSuccessfulJobRunTime == null)));

            logger.info("getLastSuccessfulJobRunTime::" + getLastSuccessfulJobRunTime);
            logger.info("maxBomUpdatedDate:: " + maxBomUpdatedDate);

            if ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime))
                    || (getLastSuccessfulJobRunTime == null)) {
                int index = 0;

                for (HubProjectVersion hubProjectVersion : hubProjectVersions) {

                    // Get the Vulnerability information
                    try {
                        vulnerableComponentViews = HubServices.getVulnerabilityComponentViews(projectVersionItems.get(index));
                        index++;
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Error while getting the Hub Vulnerabilities", e);
                    } catch (IntegrationException e) {
                        throw new RuntimeException("Error while getting the Hub Vulnerabilities", e);
                    }

                    // Convert the Hub Vulnerability component view to CSV Vulnerability object
                    List<Vulnerability> vulnerabilities = transformMapping(vulnerableComponentViews, hubProjectVersion.getHubProject(),
                            hubProjectVersion.getHubProjectVersion());
                    mergedVulnerabilities.addAll(vulnerabilities);
                }

                if (mergedVulnerabilities.size() > 0) {

                    if (hubProjectVersions.size() > 1) {
                        // Removing Duplicates within multiple Hub Project Versions.
                        mergedVulnerabilities = BlackDuckUtil.removeDuplicates(mergedVulnerabilities);
                    }

                    final String fileDir = PropertyConstants.getReportDir();
                    final String fileName = hubProjectVersions.get(0).getHubProject() + UNDERSCORE + hubProjectVersions.get(0).getHubProjectVersion()
                            + UNDERSCORE
                            + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

                    // Write the vulnerabilities to CSV
                    try {
                        CSVUtils.writeToCSV(mergedVulnerabilities, fileDir + fileName, ',');

                        // Get the file token for upload
                        String token = getFileToken();

                        // Upload the vulnerabilities CSV to Fortify
                        uploadCSV(token, fileDir + fileName, blackDuckFortifyMapperGroup.getFortifyApplicationId());

                        // Delete the file token that is created for upload
                        FortifyFileTokenApi.deleteFileToken();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }

                }
            }
        }

    }

    /**
     * Get the last successful job run time of the job by reading the batch_job_status.txt file
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private Date getLastSuccessfulJobRunTime(String fileName) throws IOException {
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
            throw new IOException("Unable to find the batch_job_status.txt file", e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while parsing the date. Please make sure date time format is yyyy/MM/dd HH:mm:ss.SSS", e);
        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
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
        logger.info("Uploading " + file.getName() + " to fortify");
        JobStatusResponse uploadVulnerabilityResponseBody = FortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId, file);
        logger.info("uploadVulnerabilityResponseBody:: " + uploadVulnerabilityResponseBody);
        if ("-10001".equalsIgnoreCase(uploadVulnerabilityResponseBody.getCode())
                && "Background submission succeeded.".equalsIgnoreCase(uploadVulnerabilityResponseBody.getMessage())) {
            if (file.exists()) {
                file.delete();
            }
            logger.info("File uploaded successfully");
        }
    }

}
