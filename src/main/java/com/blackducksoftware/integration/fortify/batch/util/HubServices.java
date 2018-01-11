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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.RiskProfile;
import com.blackducksoftware.integration.hub.api.project.ProjectService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionService;
import com.blackducksoftware.integration.hub.api.view.MetaHandler;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;

/**
 * This class will be used as REST client to access the Hub API's
 *
 * @author smanikantan
 *
 */
public final class HubServices {

    private final static Logger logger = Logger.getLogger(HubServices.class);

    private final HubServicesFactory hubServicesFactory;

    public HubServices(HubServicesFactory hubServicesFactory) {
        this.hubServicesFactory = hubServicesFactory;
    }

    /**
     * Get the Vulnerability component views
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public List<VulnerableComponentView> getVulnerabilityComponentViews(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        if (projectVersionItem != null) {
            final String vulnerabililtyBomComponentUrl = getVulnerabililtyBomComponentUrl(projectVersionItem);
            return getVulnerabililtyComponentViews(vulnerabililtyBomComponentUrl);
        }
        return new ArrayList<>();
    }

    /**
     * Get the Hub project version information
     *
     * @param projectName
     * @param versionName
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public ProjectVersionView getProjectVersion(final String projectName, final String versionName)
            throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub project and project version info for::" + projectName + ", " + versionName);
        final ProjectView projectItem = getProjectByProjectName(projectName);
        return getProjectVersion(projectItem, versionName);
    }

    /**
     * Get all Hub Projects information
     *
     * @return
     * @throws IntegrationException
     */
    public List<ProjectView> getAllProjects() throws IntegrationException {
        final ProjectService projectRequestService = hubServicesFactory.createProjectService();
        return projectRequestService.getAllProjects();
    }

    /**
     * Get the Hub Project version information based on project view
     *
     * @param project
     * @return
     * @throws IntegrationException
     */
    public List<ProjectVersionView> getProjectVersionsByProject(final ProjectView project) throws IntegrationException {
        final ProjectVersionService projectVersionRequestService = hubServicesFactory
                .createProjectVersionService();
        return projectVersionRequestService.getAllProjectVersions(project);
    }

    /**
     * Get the Hub Project information based on input project name
     *
     * @param projectName
     * @return
     * @throws IntegrationException
     */
    public ProjectView getProjectByProjectName(final String projectName) throws IntegrationException {
        logger.info("Getting Hub project info for::" + projectName);
        final ProjectService projectRequestService = hubServicesFactory.createProjectService();
        return projectRequestService.getProjectByName(projectName);
    }

    /**
     * Get the Hub project version view based on Project view and version name
     *
     * @param projectItem
     * @param versionName
     * @return
     * @throws IntegrationException
     */
    private ProjectVersionView getProjectVersion(final ProjectView projectItem, final String versionName) throws IntegrationException {
        logger.info("Getting Hub project version info for::" + versionName);
        final ProjectVersionService projectVersionRequestService = hubServicesFactory
                .createProjectVersionService();
        return projectVersionRequestService.getProjectVersion(projectItem, versionName);
    }

    /**
     * Get the Hub Vulnerability BOM component Url
     *
     * @param projectVersionItem
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getVulnerabililtyBomComponentUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaHandler metaService = new MetaHandler(new IntBufferedLogger());
        return metaService.getFirstLink(projectVersionItem, MetaHandler.VULNERABLE_COMPONENTS_LINK);
    }

    /**
     * Get the Hub Vulnerability Component views based on Vulnerability BOM component Url
     *
     * @param vulnerabililtyBomComponentUrl
     * @return
     * @throws IntegrationException
     */
    private List<VulnerableComponentView> getVulnerabililtyComponentViews(final String vulnerabililtyBomComponentUrl) throws IntegrationException {
        logger.info("Getting Hub Vulnerability info");
        final HubService hubResponseService = hubServicesFactory.createHubService();
        HubPagedRequest hubPagedRequest = hubResponseService.getHubRequestFactory().createPagedRequest(500, vulnerabililtyBomComponentUrl);
        return hubResponseService.getAllViews(hubPagedRequest, VulnerableComponentView.class, "application/vnd.blackducksoftware.list-1+json");
    }

    /**
     * Get the Hub Project version risk-profile url
     *
     * @param projectVersionItem
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getProjectVersionRiskProfileUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaHandler metaService = new MetaHandler(new IntBufferedLogger());
        return metaService.getFirstLink(projectVersionItem, MetaHandler.RISK_PROFILE_LINK);
    }

    /**
     * Get the Hub project version last BOM updated date based on project version risk-profile url
     *
     * @param projectVersionRiskProfileLink
     * @return
     * @throws IntegrationException
     */
    private RiskProfile getBomLastUpdatedAt(final String projectVersionRiskProfileLink) throws IntegrationException {
        final HubService hubResponseService = hubServicesFactory.createHubService();
        HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(projectVersionRiskProfileLink);
        return hubResponseService.getView(hubRequest, RiskProfile.class);
    }

    /**
     * Get the Hub project version last BOM updated date based on Hub project version view
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public Date getBomLastUpdatedAt(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub last BOM updated at");
        if (projectVersionItem != null) {
            final String projectVersionRiskProfileUrl = getProjectVersionRiskProfileUrl(projectVersionItem);
            RiskProfile riskProfile = getBomLastUpdatedAt(projectVersionRiskProfileUrl);

            return riskProfile.getBomLastUpdatedAt();
        }
        return null;
    }

    public PhoneHomeDataService getPhoneHomeDataService() {
        logger.info("Getting Phone Home Data Service");
        final PhoneHomeDataService phoneHomeDataService = hubServicesFactory.createPhoneHomeDataService();
        return phoneHomeDataService;
    }
}
