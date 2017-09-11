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
import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.job.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(classes = { SpringConfiguration.class, BlackDuckFortifyJobConfig.class, BatchSchedulerConfig.class, PropertyConstants.class })
public class CSVUtilsTest extends TestCase {
    private String PROJECT_NAME;

    private String VERSION_NAME;

    private Date bomUpdatedValueAt = null;

    @Autowired
    private HubServices hubServices;

    @Autowired
    private MappingParser mappingParser;

    @Autowired
    private PropertyConstants propertyConstants;

    @Override
    @Before
    public void setUp() throws JsonIOException, IOException, IntegrationException {
        final List<BlackDuckFortifyMapperGroup> blackDuckFortifyMappers = mappingParser
                .createMapping(propertyConstants.getMappingJsonPath());
        PROJECT_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProject();
        VERSION_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProjectVersion();
    }

    @Test
    public void testWriteToCSV() {
        System.out.println("Executing testWriteToCSV");
        ProjectVersionView projectVersionItem = null;
        List<VulnerableComponentView> vulnerableComponentViews;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
            vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
            bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        } catch (IntegrationException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
        assertNotNull(vulnerableComponentViews);
        assertNotNull(bomUpdatedValueAt);

        List<Vulnerability> vulnerabilities = VulnerabilityUtil.transformMapping(vulnerableComponentViews, PROJECT_NAME, VERSION_NAME,
                bomUpdatedValueAt, propertyConstants);
        try {
            // csvUtils.writeToCSV(vulnerabilities, PROJECT_NAME + "_" + VERSION_NAME + new Date(), ',');
            CSVUtils.writeToCSV(vulnerabilities, "sample.csv", ',');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
