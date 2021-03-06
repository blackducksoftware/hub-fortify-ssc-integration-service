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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.job.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.service.FortifyUnifiedLoginTokenApi;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.VulnerableComponentView;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { MappingParser.class, VulnerabilityUtil.class })
@ContextConfiguration(classes = { PropertyConstants.class, AttributeConstants.class, SpringConfiguration.class, BlackDuckFortifyJobConfig.class,
        BatchSchedulerConfig.class })
public class HubServicesTest extends TestCase {
    private String PROJECT_NAME;

    private String VERSION_NAME;

    @Autowired
    private MappingParser mappingParser;

    @Autowired
    private PropertyConstants propertyConstants;

    @Autowired
    private HubServices hubServices;

    @Autowired
    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    @Autowired
    private FortifyUnifiedLoginTokenApi fortifyUnifiedLoginTokenApi;

    @Override
    @Before
    public void setUp() throws JsonIOException, IOException, IntegrationException {
        final List<BlackDuckFortifyMapperGroup> blackDuckFortifyMappers = mappingParser.createMapping(propertyConstants.getMappingJsonPath());
        PROJECT_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProject();
        VERSION_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProjectVersion();
    }

    @Test
    public void hubServiceTest() throws JsonIOException, IOException, IntegrationException {
        getBomLastUpdatedAt();
        getProjectVersion();
        getVulnerability();
        getProjectVersionWithInvalidProjectName();
        getProjectVersionWithInvalidVersionName();
        cleanUpUnifiedLoginToken();
    }

    public void getBomLastUpdatedAt() throws IntegrationException {
        System.out.println("Executing getBomLastUpdatedAt");
        final ProjectVersionView projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        final Date bomLastUpdatedAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
        System.out.println("bomLastUpdatedAt::" + bomLastUpdatedAt);
        // assertNotNull(bomLastUpdatedAt);
    }

    public void getProjectVersion() throws IntegrationException {
        System.out.println("Executing getProjectVersion");
        ProjectVersionView projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        System.out.println("projectVersionItem: " + projectVersionItem);
        // assertNotNull(projectVersionItem);
    }

    public void getProjectVersionWithInvalidProjectName() {
        System.out.println("Executing getProjectVersionWithInvalidProjectName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion("Solr1", VERSION_NAME);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        } catch (final IntegrationException e) {
            // e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            // assertTrue(e.getMessage().contains("This Project does not exist"));
        }
        assertNull(projectVersionItem);
    }

    public void getProjectVersionWithInvalidVersionName() {
        System.out.println("Executing getProjectVersionWithInvalidVersionName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, "3.10");
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        } catch (final IntegrationException e) {
            // e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            // assertTrue(e.getMessage().contains("Could not find the version"));
        }
        assertNull(projectVersionItem);
    }

    public void getVulnerability() throws IntegrationException {
        System.out.println("Executing getVulnerability");
        ProjectVersionView projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        final List<VulnerableComponentView> vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size());
        // assertNotNull(vulnerableComponentViews);
    }

    private void cleanUpUnifiedLoginToken() throws JsonIOException, IOException, IntegrationException {
        if (blackDuckFortifyJobConfig.getFortifyToken().getData() != null && blackDuckFortifyJobConfig.getFortifyToken().getData().getId() != 0) {
            fortifyUnifiedLoginTokenApi.deleteUnifiedLoginToken(blackDuckFortifyJobConfig.getFortifyToken().getData().getId());
        }
    }
}
