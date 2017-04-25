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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

@Configuration
public class HubUtilities {
    private static HubServicesFactory hubServicesFactory;

    @Autowired
    private RestConnectionHelper restConnectionHelper;

    public List<VulnerableComponentView> getVulnerabilityComponentViews(final String projectName, final String versionName)
            throws IllegalArgumentException, IntegrationException {
        hubServicesFactory = restConnectionHelper.createHubServicesFactory();
        final ProjectView projectItem = getProjectView(projectName);
        final ProjectVersionView projectVersionItem = getProjectVersionRequestService(projectItem, versionName);
        final String vulnerabililtyBomComponentUrl = getVulnerabililtyBomComponentUrl(projectVersionItem);
        System.out.println("vulnerabililtyBomComponentUrl::" + vulnerabililtyBomComponentUrl);
        return getVulnerabililtyComponentViews(vulnerabililtyBomComponentUrl);
    }

    private ProjectView getProjectView(final String projectName) throws IntegrationException {
        final ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService(hubServicesFactory.getRestConnection().logger);
        return projectRequestService.getProjectByName(projectName);
    }

    private ProjectVersionView getProjectVersionRequestService(final ProjectView projectItem, final String versionName) throws IntegrationException {
        final ProjectVersionRequestService projectVersionRequestService = hubServicesFactory
                .createProjectVersionRequestService(hubServicesFactory.getRestConnection().logger);
        return projectVersionRequestService.getProjectVersion(projectItem, versionName);
    }

    private String getVulnerabililtyBomComponentUrl(final ProjectVersionView projectVersionItem) throws HubIntegrationException {
        final MetaService metaService = hubServicesFactory.createMetaService(hubServicesFactory.getRestConnection().logger);
        return metaService.getFirstLink(projectVersionItem, MetaService.VULNERABLE_COMPONENTS_LINK);
    }

    private List<VulnerableComponentView> getVulnerabililtyComponentViews(final String vulnerabililtyBomComponentUrl) throws IntegrationException {
        final HubResponseService hubResponseService = new HubResponseService(hubServicesFactory.getRestConnection());
        HubPagedRequest hubPagedRequest = new HubPagedRequest(hubServicesFactory.getRestConnection());
        hubPagedRequest.url = vulnerabililtyBomComponentUrl;
        return hubResponseService.getAllItems(hubPagedRequest, VulnerableComponentView.class);
    }
}
