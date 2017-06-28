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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

/**
 * Fortify API Tests
 *
 * @author hsathe
 *
 */
@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = TestApplication.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ FortifyService.class, OkHttpClient.class, OkHttpClient.Builder.class, FortifyApplicationVersionApi.class, FortifyApplicationResponse.class,
        CreateApplicationRequest.class })

public class FortifyApplicationVersionApiTest extends TestCase {
    @Mock
    OkHttpClient okHttpClient;

    String FIELDS = "id";

    String QUERY = "name:1.3+and+project.name:Logistics";

    @Override
    @Before
    public void setUp() {
        PowerMockito.mockStatic(OkHttpClient.Builder.class);
        OkHttpClient.Builder builder = Mockito.mock(OkHttpClient.Builder.class);

        PowerMockito.mockStatic(FortifyService.class);
        Mockito.when(FortifyService.getHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(builder);

        PowerMockito.mockStatic(OkHttpClient.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(builder.build()).thenReturn(okHttpClient);

        PowerMockito.mockStatic(FortifyApplicationVersionApi.class);
    }

    @Test
    public void getApplicationVersionTest() throws IOException, IntegrationException {
        System.out.println("Executing getApplicationVersionTest");
        PowerMockito.mock(FortifyApplicationResponse.class);
        FortifyApplicationResponse mockResponse = Mockito.mock(FortifyApplicationResponse.class);

        Mockito.when(FortifyApplicationVersionApi.getApplicationVersionByName(Mockito.anyString(), Mockito.anyString())).thenReturn(mockResponse);
        FortifyApplicationResponse response = FortifyApplicationVersionApi.getApplicationVersionByName(FIELDS, QUERY);
        assertNotNull(response);
    }

    @Test
    public void createApplicationVersionTest() throws IOException {
        System.out.println("Executing createApplicationVersionTest");
        PowerMockito.mock(CreateApplicationRequest.class);
        CreateApplicationRequest mockCreateApplicationRequest = Mockito.mock(CreateApplicationRequest.class);
        Mockito.when(FortifyApplicationVersionApi.createApplicationVersion(Mockito.any())).thenReturn(110);
        int id = FortifyApplicationVersionApi.createApplicationVersion(mockCreateApplicationRequest);
        assertEquals("Created application", 110, id);

    }

    @Test
    public void updateApplicationAttributesTest() throws IOException, IntegrationException {
        System.out.println("Executing updateApplicationAttributesTest");
        int parentId = 111;

        String attributeValues = "[{\"attributeDefinitionId\":5,\"values\":[{\"guid\":\"New\"}],\"value\":null},{\"attributeDefinitionId\":6,\"values\":[{\"guid\":\"Internal\"}],\"value\":null},{\"attributeDefinitionId\":7,\"values\":[{\"guid\":\"internalnetwork\"}],\"value\":null},{\"attributeDefinitionId\":10,\"values\":[],\"value\":null},{\"attributeDefinitionId\":11,\"values\":[],\"value\":null},{\"attributeDefinitionId\":12,\"values\":[],\"value\":null},{\"attributeDefinitionId\":1,\"values\":[{\"guid\":\"High\"}],\"value\":null},{\"attributeDefinitionId\":2,\"values\":[],\"value\":null},{\"attributeDefinitionId\":3,\"values\":[],\"value\":null},{\"attributeDefinitionId\":4,\"values\":[],\"value\":null}]";
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UpdateFortifyApplicationAttributesRequest>>() {
        }.getType();

        List<UpdateFortifyApplicationAttributesRequest> request = gson.fromJson(attributeValues, listType);
        Mockito.when(FortifyApplicationVersionApi.updateApplicationAttributes(Mockito.anyInt(), Mockito.any())).thenReturn(201);
        int responseCode = FortifyApplicationVersionApi.updateApplicationAttributes(parentId, request);
        assertEquals("Updated application attributes", 201, responseCode);
    }

    @Test
    public void commitApplicationVersion() throws IOException, IntegrationException {
        System.out.println("Executing commitApplicationVersion");
        final int ID = 111;
        CommitFortifyApplicationRequest request = new CommitFortifyApplicationRequest(true);
        Mockito.when(FortifyApplicationVersionApi.commitApplicationVersion(Mockito.anyInt(), Mockito.any())).thenReturn(201);
        int responseCode = FortifyApplicationVersionApi.commitApplicationVersion(ID, request);
        assertEquals("Committed application attributes", 201, responseCode);
    }

}
