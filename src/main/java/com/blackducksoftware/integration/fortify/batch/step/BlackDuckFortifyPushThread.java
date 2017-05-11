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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

@Component
public class BlackDuckFortifyPushThread implements Runnable {

    private HubServices hubServices;

    private Environment env;

    private FortifyFileTokenApi fortifyFileTokenApi;

    private FortifyUploadApi fortifyUploadApi;

    private CSVUtils csvUtils;

    private BlackDuckFortifyMapper blackDuckFortifyMapper;

    private Date bomUpdatedValueAt;

    private final String UNDERSCORE = "_";

    private final Function<VulnerableComponentView, Vulnerability> transformMapping = new Function<VulnerableComponentView, Vulnerability>() {

        @Override
        public Vulnerability apply(VulnerableComponentView vulnerableComponentView) {
            Vulnerability vulnerability = new Vulnerability();
            String[] componentVersionLinkArr = vulnerableComponentView.getComponentVersionLink().split("/");
            vulnerability.setProjectId(String.valueOf(componentVersionLinkArr[5]));
            vulnerability.setVersionId(String.valueOf(componentVersionLinkArr[7]));
            vulnerability.setChannelVersionId("");
            vulnerability.setProjectName(String.valueOf(vulnerableComponentView.getComponentName()));
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
            vulnerability.setSeverity(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSeverity()));
            vulnerability.setRemediationStatus(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationStatus()));
            vulnerability.setRemediationComment(String.valueOf(""));
            vulnerability.setUrl("NVD".equalsIgnoreCase(vulnerableComponentView.getVulnerabilityWithRemediation().getSource())
                    ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()
                    : "");
            vulnerability.setScanDate(bomUpdatedValueAt);
            return vulnerability;
        }
    };

    @Autowired
    public BlackDuckFortifyPushThread(BlackDuckFortifyMapper blackDuckFortifyMapper, HubServices hubServices, Environment env,
            FortifyFileTokenApi fortifyFileTokenApi, FortifyUploadApi fortifyUploadApi, CSVUtils csvUtils) {
        this.blackDuckFortifyMapper = blackDuckFortifyMapper;
        this.hubServices = hubServices;
        this.env = env;
        this.fortifyFileTokenApi = fortifyFileTokenApi;
        this.fortifyUploadApi = fortifyUploadApi;
        this.csvUtils = csvUtils;
    }

    @Override
    public void run() {
        if (blackDuckFortifyMapper != null) {
            System.out.println("blackDuckFortifyMapper::" + blackDuckFortifyMapper.toString());
            ProjectVersionView projectVersionItem = null;
            List<VulnerableComponentView> vulnerableComponentViews;
            try {
                projectVersionItem = hubServices.getProjectVersion(blackDuckFortifyMapper.getHubProject(), blackDuckFortifyMapper.getHubProjectVersion());
                vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
                bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
                List<Vulnerability> vulnerabilities = vulnerableComponentViews.stream().map(transformMapping).collect(Collectors.<Vulnerability> toList());

                final String fileDir = env.getProperty("hub.fortify.report.dir");
                final String fileName = blackDuckFortifyMapper.getHubProject() + UNDERSCORE + blackDuckFortifyMapper.getHubProjectVersion() + UNDERSCORE
                        + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

                csvUtils.writeToCSV(vulnerabilities, fileDir + fileName, ',');

                String token = getFileToken();
                System.out.println("Token::" + token);
                uploadCSV(token, fileDir + fileName, blackDuckFortifyMapper.getFortifyApplicationId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            } catch (IntegrationException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private String getFileToken() throws IOException {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        FileTokenResponse fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
        return fileTokenResponse.getData().getToken();
    }

    private void uploadCSV(String token, String fileName, int fortifyApplicationId) throws IOException {
        File file = new File(fileName);
        System.out.println("file::" + file);
        JobStatusResponse uploadVulnerabilityResponseBody = fortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId, file);
        if ("-10001".equalsIgnoreCase(uploadVulnerabilityResponseBody.getCode())
                && "Background submission succeeded.".equalsIgnoreCase(uploadVulnerabilityResponseBody.getMessage())) {
            if (file.exists()) {
                file.delete();
            }
        }
        System.out.println("uploadVulnerabilityResponseBody::" + uploadVulnerabilityResponseBody.toString());
    }
}
