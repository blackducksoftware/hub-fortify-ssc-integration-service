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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.RiskProfile;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

public class HubServices {

    private static HubServicesFactory hubServicesFactory;

    public HubServices() {
        try {
            hubServicesFactory = RestConnectionHelper.createHubServicesFactory();
        } catch (IllegalArgumentException | EncryptionException | HubIntegrationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<VulnerableComponentView> getVulnerabilityComponentViews(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        if (projectVersionItem != null) {
            final String vulnerabililtyBomComponentUrl = getVulnerabililtyBomComponentUrl(projectVersionItem);
            return getVulnerabililtyComponentViews(vulnerabililtyBomComponentUrl);
        }
        return new ArrayList<>();
    }

    public ProjectVersionView getProjectVersion(final String projectName, final String versionName) throws IllegalArgumentException, IntegrationException {
        final ProjectView projectItem = getProjectByProjectName(projectName);
        return getProjectVersion(projectItem, versionName);
    }

    public List<ProjectView> getAllProjects() throws IntegrationException {
        final ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService(hubServicesFactory.getRestConnection().logger);
        return projectRequestService.getAllProjects();
    }

    public List<ProjectVersionView> getProjectVersionsByProject(final ProjectView project) throws IntegrationException {
        final ProjectVersionRequestService projectVersionRequestService = hubServicesFactory
                .createProjectVersionRequestService(hubServicesFactory.getRestConnection().logger);
        return projectVersionRequestService.getAllProjectVersions(project);
    }

    public ProjectView getProjectByProjectName(final String projectName) throws IntegrationException {
        final ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService(hubServicesFactory.getRestConnection().logger);
        return projectRequestService.getProjectByName(projectName);
    }

    private ProjectVersionView getProjectVersion(final ProjectView projectItem, final String versionName) throws IntegrationException {
        final ProjectVersionRequestService projectVersionRequestService = hubServicesFactory
                .createProjectVersionRequestService(hubServicesFactory.getRestConnection().logger);
        return projectVersionRequestService.getProjectVersion(projectItem, versionName);
    }

    private String getVulnerabililtyBomComponentUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaService metaService = hubServicesFactory.createMetaService(hubServicesFactory.getRestConnection().logger);
        return metaService.getFirstLink(projectVersionItem, MetaService.VULNERABLE_COMPONENTS_LINK);
    }

    private List<VulnerableComponentView> getVulnerabililtyComponentViews(final String vulnerabililtyBomComponentUrl) throws IntegrationException {
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubPagedRequest hubPagedRequest = hubResponseService.getHubRequestFactory().createPagedRequest(vulnerabililtyBomComponentUrl);
        return hubResponseService.getAllItems(hubPagedRequest, VulnerableComponentView.class);
    }

    private String getProjectVersionRiskProfileUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaService metaService = hubServicesFactory.createMetaService(hubServicesFactory.getRestConnection().logger);
        return metaService.getFirstLink(projectVersionItem, MetaService.RISK_PROFILE_LINK);
    }

    private RiskProfile getBomLastUpdatedAt(final String projectVersionRiskProfileLink) throws IntegrationException {
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(projectVersionRiskProfileLink);
        return hubResponseService.getItem(hubRequest, RiskProfile.class);
    }

    public Date getBomLastUpdatedAt(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        if (projectVersionItem != null) {
            final String projectVersionRiskProfileUrl = getProjectVersionRiskProfileUrl(projectVersionItem);
            RiskProfile riskProfile = getBomLastUpdatedAt(projectVersionRiskProfileUrl);
            return riskProfile.getBomLastUpdatedAt();
        }
        return null;
    }
}
