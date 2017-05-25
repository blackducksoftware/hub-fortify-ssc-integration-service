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
package com.blackducksoftware.integration.fortify.batch.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
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

    private BlackDuckFortifyMapper blackDuckFortifyMapper;

    private Date bomUpdatedValueAt;

    private final String UNDERSCORE = "_";

    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

    private final Function<VulnerableComponentView, Vulnerability> transformMapping = new Function<VulnerableComponentView, Vulnerability>() {

        @Override
        public Vulnerability apply(VulnerableComponentView vulnerableComponentView) {
            Vulnerability vulnerability = new Vulnerability();
            vulnerability.setProjectName(String.valueOf(blackDuckFortifyMapper.getHubProject()));
            vulnerability.setProjectVersion(String.valueOf(blackDuckFortifyMapper.getHubProjectVersion()));
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
            vulnerability.setScanDate(bomUpdatedValueAt);
            return vulnerability;
        }
    };

    public BlackDuckFortifyPushThread(BlackDuckFortifyMapper blackDuckFortifyMapper) {
        this.blackDuckFortifyMapper = blackDuckFortifyMapper;
    }

    @Override
    public void run() {
        if (blackDuckFortifyMapper != null) {
            logger.info("Mapping was successfully created");
            logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapper.toString());

            ProjectVersionView projectVersionItem = null;
            List<VulnerableComponentView> vulnerableComponentViews;
            try {
                // Get the project version
                projectVersionItem = HubServices.getProjectVersion(blackDuckFortifyMapper.getHubProject(), blackDuckFortifyMapper.getHubProjectVersion());

                // Get the Last BOM updated date
                bomUpdatedValueAt = HubServices.getBomLastUpdatedAt(projectVersionItem);

                // Get the last successful runtime of the job
                final Date getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(PropertyConstants.getBatchJobStatusFilePath());
                logger.info("Last successful job excecution:" + getLastSuccessfulJobRunTime);

                logger.info("Compare Dates: " + ((getLastSuccessfulJobRunTime != null && bomUpdatedValueAt.after(getLastSuccessfulJobRunTime))
                        || (getLastSuccessfulJobRunTime == null)));

                if ((getLastSuccessfulJobRunTime != null && bomUpdatedValueAt.after(getLastSuccessfulJobRunTime))
                        || (getLastSuccessfulJobRunTime == null)) {
                    // Get the Vulnerability information
                    vulnerableComponentViews = HubServices.getVulnerabilityComponentViews(projectVersionItem);
                    List<Vulnerability> vulnerabilities = vulnerableComponentViews.stream().map(transformMapping).collect(Collectors.<Vulnerability> toList());
                    if (vulnerabilities.size() > 0) {
                        final String fileDir = PropertyConstants.getReportDir();
                        final String fileName = blackDuckFortifyMapper.getHubProject() + UNDERSCORE + blackDuckFortifyMapper.getHubProjectVersion() + UNDERSCORE
                                + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

                        // Write the vulnerabilities to CSV
                        CSVUtils.writeToCSV(vulnerabilities, fileDir + fileName, ',');

                        // Get the file token for upload
                        String token = getFileToken();

                        // Upload the vulnerabilities CSV to Fortify
                        uploadCSV(token, fileDir + fileName, blackDuckFortifyMapper.getFortifyApplicationId());

                        // Delete the file token that is created for upload
                        FortifyFileTokenApi.deleteFileToken();
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage(), e);
            } catch (IntegrationException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * Get the last successful job run time of the job by reading the batch_job_status.txt file
     *
     * @param fileName
     * @return
     */
    private Date getLastSuccessfulJobRunTime(String fileName) {
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
            e.printStackTrace();
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
     * @throws Exception
     */
    private void uploadCSV(String token, String fileName, int fortifyApplicationId) throws Exception {
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
