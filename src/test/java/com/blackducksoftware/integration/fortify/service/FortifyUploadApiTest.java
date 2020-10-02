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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
import com.blackducksoftware.integration.fortify.batch.util.AttributeConstants;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { MappingParser.class, FortifyApplicationVersionApi.class, FortifyFileTokenApi.class, FortifyUploadApi.class })
@ContextConfiguration(classes = { PropertyConstants.class, AttributeConstants.class, SpringConfiguration.class, BlackDuckFortifyJobConfig.class,
        BatchSchedulerConfig.class, String.class })
public class FortifyUploadApiTest extends TestCase {

    @Autowired
    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;
    
    @Autowired
    private FortifyUnifiedLoginTokenApi fortifyUnifiedLoginTokenApi;

    @Autowired
    private MappingParser mappingParser;

    private FortifyUploadApi fortifyUploadApi;

    private FortifyFileTokenApi fortifyFileTokenApi;

    private FortifyApplicationVersionApi fortifyApplicationVersionApi;

    @Override
    @Before
    public void setUp() throws IOException, IntegrationException {
        fortifyApplicationVersionApi = blackDuckFortifyJobConfig.getFortifyApplicationVersionApi();
        fortifyFileTokenApi = blackDuckFortifyJobConfig.getFortifyFileTokenApi();
        fortifyUploadApi = blackDuckFortifyJobConfig.getFortifyUploadApi();
    }

    @Test
    public void uploadCSVFile() throws Exception {
        int id = 0;
        try {
            CreateApplicationRequest createApplicationRequest = createApplicationVersionRequest("Fortify-Test10", "1.0");
            id = fortifyApplicationVersionApi.createApplicationVersion(createApplicationRequest);
            assertNotNull(id);
            updateApplicationAttributesTest(id);
            commitApplicationVersion(id);

            System.out.println("Executing uploadCSVFile");
            FileToken fileToken = new FileToken("UPLOAD");

            String fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
            System.out.println("File Token::" + fileTokenResponse);
            Assert.assertNotNull(fileTokenResponse);

            File file = new File("sample.csv");
            // File file = Mockito.mock(File.class);
            // File file = new
            // File("/Users/smanikantan/Documents/hub-fortify-integration/report/solrWar2_4.10.4_20170510160506866.csv");
            System.out.println("file::" + file);

            boolean response = fortifyUploadApi.uploadVulnerabilityByProjectVersion(fileTokenResponse, id, file);
            System.out.println("uploadVulnerabilityResponse::" + response);
            Assert.assertTrue(response);
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
        List<UpdateFortifyApplicationAttributesRequest> request = mappingParser.addCustomAttributes();
        fortifyApplicationVersionApi.updateApplicationAttributes(parentId, request);
    }

    public void commitApplicationVersion(int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing commitApplicationVersion");
        CommitFortifyApplicationRequest request = new CommitFortifyApplicationRequest(true);
        fortifyApplicationVersionApi.commitApplicationVersion(applicationId, request);
    }

    public void deleteApplicationVersion(int applicationId) throws IOException, IntegrationException {
        System.out.println("Executing deleteApplicationVersion");
        fortifyApplicationVersionApi.deleteApplicationVersion(applicationId);
    }

    private CreateApplicationRequest createApplicationVersionRequest(String fortifyProjectName, String fortifyProjectVersion) {
        String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        return new CreateApplicationRequest(fortifyProjectVersion, "Built using API", true, false,
                new CreateApplicationRequest.Project("", fortifyProjectName, "Built using API", TEMPLATE), TEMPLATE);
    }
    
    @Override
    @After
    public void tearDown() throws JsonIOException, IOException, IntegrationException {
        if (blackDuckFortifyJobConfig.getFortifyToken().getData() != null && blackDuckFortifyJobConfig.getFortifyToken().getData().getId() != 0) {
            fortifyUnifiedLoginTokenApi.deleteUnifiedLoginToken(blackDuckFortifyJobConfig.getFortifyToken().getData().getId());
        }
    }
}
