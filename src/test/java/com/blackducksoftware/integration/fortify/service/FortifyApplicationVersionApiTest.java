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
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

/**
 * Fortify API Tests
 *
 * @author hsathe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class FortifyApplicationVersionApiTest extends TestCase {

    String FIELDS = "id";

    String QUERY = "name:1.3+and+project.name:Logistics";

    private SpringConfiguration springConfiguration;

    @Override
    @Before
    public void setUp() {
        springConfiguration = new SpringConfiguration();
    }

    @Test
    public void getApplicationVersionTest() throws IOException, IntegrationException {
        System.out.println("Executing getApplicationVersionTest");
        FortifyApplicationResponse response = springConfiguration.getFortifyApplicationVersionApi().getApplicationVersionByName(FIELDS, QUERY);
        assertNotNull(response);
    }

    @Test
    public void createApplicationVersionTest() throws IOException, IntegrationException {
        System.out.println("Executing createApplicationVersionTest");
        CreateApplicationRequest createApplicationRequest = createApplicationVersionRequest("Fortify-Test", "1.0");
        int id = springConfiguration.getFortifyApplicationVersionApi().createApplicationVersion(createApplicationRequest);
        assertNotNull(id);
        try {
            updateApplicationAttributesTest(id);
            commitApplicationVersion(id);
        } catch (IOException e) {
            throw new IOException(e);
        } catch (IntegrationException e) {
            throw new IntegrationException(e);
        } finally {
            deleteApplicationVersion(id);
        }
    }

    public void updateApplicationAttributesTest(int parentId) throws IOException, IntegrationException {
        System.out.println("Executing updateApplicationAttributesTest");

        String attributeValues = "[{\"attributeDefinitionId\":5,\"values\":[{\"guid\":\"New\"}],\"value\":null},{\"attributeDefinitionId\":6,\"values\":[{\"guid\":\"Internal\"}],\"value\":null},{\"attributeDefinitionId\":7,\"values\":[{\"guid\":\"internalnetwork\"}],\"value\":null},{\"attributeDefinitionId\":10,\"values\":[],\"value\":null},{\"attributeDefinitionId\":11,\"values\":[],\"value\":null},{\"attributeDefinitionId\":12,\"values\":[],\"value\":null},{\"attributeDefinitionId\":1,\"values\":[{\"guid\":\"High\"}],\"value\":null},{\"attributeDefinitionId\":2,\"values\":[],\"value\":null},{\"attributeDefinitionId\":3,\"values\":[],\"value\":null},{\"attributeDefinitionId\":4,\"values\":[],\"value\":null}]";
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UpdateFortifyApplicationAttributesRequest>>() {
        }.getType();

        List<UpdateFortifyApplicationAttributesRequest> request = gson.fromJson(attributeValues, listType);
        request = springConfiguration.getMappingParser().addCustomAttributes(request);
        int responseCode = springConfiguration.getFortifyApplicationVersionApi().updateApplicationAttributes(parentId, request);
        assertEquals("Updated application attributes", 200, responseCode);
    }

    public void commitApplicationVersion(int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing commitApplicationVersion");
        CommitFortifyApplicationRequest request = new CommitFortifyApplicationRequest(true);
        int responseCode = springConfiguration.getFortifyApplicationVersionApi().commitApplicationVersion(applicationId, request);
        assertEquals("Committed application attributes", 200, responseCode);
    }

    public void deleteApplicationVersion(int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing deleteApplicationVersion");
        int responseCode = springConfiguration.getFortifyApplicationVersionApi().deleteApplicationVersion(applicationId);
        assertEquals("Delete application version", 200, responseCode);
    }

    private CreateApplicationRequest createApplicationVersionRequest(String fortifyProjectName, String fortifyProjectVersion) {
        String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        return new CreateApplicationRequest(fortifyProjectVersion, "Built using API", true, false,
                new CreateApplicationRequest.Project("", fortifyProjectName, "Built using API", TEMPLATE), TEMPLATE);
    }
}
