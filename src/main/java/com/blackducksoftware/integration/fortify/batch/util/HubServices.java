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

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.VersionRiskProfileView;
import com.blackducksoftware.integration.fortify.hub.model.Recommendation;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.api.view.MetaHandler;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.service.HubRegistrationService;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;
import com.blackducksoftware.integration.hub.service.model.RequestFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.rest.connection.RestConnection;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.util.IntEnvironmentVariables;
import com.google.gson.Gson;

/**
 * This class will be used as REST client to access the Hub API's
 *
 * @author smanikantan
 *
 */
public final class HubServices {

    private final static Logger logger = Logger.getLogger(HubServices.class);

    public static final String ALLIANCES_TRACKING_ID = "UA-116682967-3";

    private final HubServicesFactory hubServicesFactory;

    public HubServices(final HubServicesFactory hubServicesFactory) {
        this.hubServicesFactory = hubServicesFactory;
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
    public ProjectVersionView getProjectVersion(final String projectName, final String projectVersionName)
            throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub project and project version info for::" + projectName + ", " + projectVersionName);
        final ProjectService projectVersionRequestService = hubServicesFactory.createProjectService();
        final ProjectVersionWrapper projectVersionWrapper = projectVersionRequestService.getProjectVersion(projectName, projectVersionName);
        if (projectVersionWrapper != null && projectVersionWrapper.getProjectVersionView() != null) {
            logger.trace("ProjectVersionView::" + projectVersionWrapper.getProjectVersionView().json);
            return projectVersionWrapper.getProjectVersionView();
        } else {
            throw new IntegrationException("Project Version does not Exists!");
        }
    }

    /**
     * Get the Vulnerability component views
     *
     * @param projectVersionItem
     * @return
     * @throws IntegrationException
     */
    public List<VulnerableComponentView> getVulnerabilityComponentViews(final ProjectVersionView projectVersionItem) throws IntegrationException {
        logger.info("Getting Hub Vulnerability info");
        if (projectVersionItem != null) {
            final HubService hubResponseService = hubServicesFactory.createHubService();
            final Request.Builder requestBuilder = RequestFactory.createCommonGetRequestBuilder().addQueryParameter("limit", String.valueOf(500))
                    .mimeType("application/vnd.blackducksoftware.list-1+json");
            return hubResponseService.getResponses(projectVersionItem, ProjectVersionView.VULNERABLE_COMPONENTS_LINK_RESPONSE, requestBuilder, true);
        }
        return new ArrayList<>();
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
        return metaService.getFirstLink(projectVersionItem, ProjectVersionView.RISKPROFILE_LINK);
    }

    /**
     * Get the Hub project version last BOM updated date for the given project version
     *
     * @param projectVersionItem
     * @return
     * @throws IntegrationException
     */
    public Date getBomLastUpdatedAt(final ProjectVersionView projectVersionItem) throws IntegrationException {
        logger.info("Getting Hub project version last BOM updated date");
        if (projectVersionItem != null) {
            final String riskProfileLink = getProjectVersionRiskProfileUrl(projectVersionItem);
            logger.debug("riskProfileLink::" + riskProfileLink);
            final HubService hubResponseService = hubServicesFactory.createHubService();
            final VersionRiskProfileView versionRiskProfileView = hubResponseService.getResponse(riskProfileLink, VersionRiskProfileView.class);
            // logger.debug("versionRiskProfileView::" + versionRiskProfileView.json);
            return versionRiskProfileView.getBomLastUpdatedAt();
        }
        return null;
    }

    /**
     * Get the Black Duck component version recommendations for the given component version
     *
     * @param componentVersionRemediatingUrl
     * @return
     * @throws IntegrationException
     */
    public Recommendation getComponentVersionRecommendations(final String componentVersionRemediatingUrl) throws IntegrationException {
        logger.debug("Getting Black Duck component version remediating");
        Recommendation recommendation = null;
        if (componentVersionRemediatingUrl != null) {
            final HubService hubResponseService = hubServicesFactory.createHubService();
            try {
                recommendation = hubResponseService.getResponse(componentVersionRemediatingUrl, Recommendation.class);
            } catch (final IntegrationException e) {
                if (!e.getMessage().contains("Error: 404 : 404")) {
                    throw new IntegrationException(e);
                }
            }
        }
        return recommendation;
    }

    public HubService createHubService() {
        logger.info("Creating Hub service");
        return hubServicesFactory.createHubService();
    }

    public HubRegistrationService createHubRegistrationService() {
        logger.info("Creating Hub registration service");
        return hubServicesFactory.createHubRegistrationService();
    }

    public PhoneHomeService createPhoneHomeDataService() {
        logger.info("Creating Phone home data service");
        return new PhoneHomeService(createHubService(), createPhoneHomeClient(), createHubRegistrationService(), new IntEnvironmentVariables(true));
    }

    public PhoneHomeClient createPhoneHomeClient() {
        logger.info("Creating Phone home client");
        final RestConnection restConnection = hubServicesFactory.getRestConnection();
        final HttpClientBuilder httpClientBuilder = restConnection.getClientBuilder();
        final Gson gson = restConnection.gson;
        return new PhoneHomeClient(ALLIANCES_TRACKING_ID, httpClientBuilder, gson);
    }
}
