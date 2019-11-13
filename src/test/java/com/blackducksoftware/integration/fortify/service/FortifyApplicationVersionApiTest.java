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
package com.blackducksoftware.integration.fortify.service;

import java.io.IOException;
import java.util.List;

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
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;

import junit.framework.TestCase;

/**
 * Fortify API Tests
 *
 * @author hsathe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(classes = { SpringConfiguration.class, BlackDuckFortifyJobConfig.class, BatchSchedulerConfig.class, PropertyConstants.class })
public class FortifyApplicationVersionApiTest extends TestCase {

    String FIELDS = "id";

    String QUERY = "name:1.0%2Band%2Bproject.name:HUB-FORTIFY-TEST1";

    @Autowired
    private FortifyApplicationVersionApi fortifyApplicationVersionApi;

    @Autowired
    private MappingParser mappingParser;

    @Test
    public void getApplicationVersionTest() throws IOException, IntegrationException {
        System.out.println("Executing getApplicationVersionTest");
        final FortifyApplicationResponse response = fortifyApplicationVersionApi.getApplicationVersionByName(FIELDS, QUERY);
        assertTrue(response.getData().size() > 0);
    }

    @Test
    public void createApplicationVersionTest() throws IOException, IntegrationException {
        System.out.println("Executing createApplicationVersionTest");
        final CreateApplicationRequest createApplicationRequest = createApplicationVersionRequest("Fortify-Test1", "1|2");
        final int id = fortifyApplicationVersionApi.createApplicationVersion(createApplicationRequest);
        assertNotNull(id);
        try {
            updateApplicationAttributesTest(id);
            commitApplicationVersion(id);
        } catch (final IOException e) {
            throw new IOException(e);
        } catch (final IntegrationException e) {
            throw new IntegrationException(e);
        } finally {
            deleteApplicationVersion(id);
        }
    }

    public void updateApplicationAttributesTest(final int parentId) throws IOException, IntegrationException {
        System.out.println("Executing updateApplicationAttributesTest");
        final List<UpdateFortifyApplicationAttributesRequest> request = mappingParser.addCustomAttributes();
        fortifyApplicationVersionApi.updateApplicationAttributes(parentId, request);
    }

    public void commitApplicationVersion(final int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing commitApplicationVersion");
        final CommitFortifyApplicationRequest request = new CommitFortifyApplicationRequest(true);
        fortifyApplicationVersionApi.commitApplicationVersion(applicationId, request);
    }

    public void deleteApplicationVersion(final int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing deleteApplicationVersion");
        fortifyApplicationVersionApi.deleteApplicationVersion(applicationId);
    }

    private CreateApplicationRequest createApplicationVersionRequest(final String fortifyProjectName, final String fortifyProjectVersion) {
        final String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        return new CreateApplicationRequest(fortifyProjectVersion, "Built using API", true, false,
                new CreateApplicationRequest.Project("", fortifyProjectName, "Built using API", TEMPLATE), TEMPLATE);
    }
}
