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

import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FortifyService.class, OkHttpClient.class, OkHttpClient.Builder.class, FortifyApplicationVersionApi.class })

public class FortifyApplicationVersionApiTest extends TestCase {
    @Mock
    OkHttpClient okHttpClient;

    String FIELDS = "id";

    String QUERY = "name:1.3+and+project.name:Logistics";

    @Mock
    CreateApplicationRequest request;

    @Test
    public void getApplicationVersionTest() throws IOException {
        FortifyApplicationResponse mockResponse = new FortifyApplicationResponse();

        Mockito.when(FortifyApplicationVersionApi.getApplicationByName(Mockito.anyString(), Mockito.anyString())).thenReturn(mockResponse);
        FortifyApplicationResponse response = FortifyApplicationVersionApi.getApplicationByName(FIELDS, QUERY);
        assertNotNull(response);
    }

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
    public void createApplicationVersionTest() throws IOException {

        Mockito.when(FortifyApplicationVersionApi.createApplicationVersion(Mockito.any())).thenReturn(110);
        int id = FortifyApplicationVersionApi.createApplicationVersion(request);
        assertEquals("Created application", 110, id);

    }

    @Test
    public void updateApplicationAttributesTest() throws IOException {
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
    public void commitApplicationVersion() throws IOException {
        final int ID = 111;
        CommitFortifyApplicationRequest request = new CommitFortifyApplicationRequest();
        request.setCommitted(true);
        Mockito.when(FortifyApplicationVersionApi.commitApplicationVersion(Mockito.anyInt(), Mockito.any())).thenReturn(201);
        int responseCode = FortifyApplicationVersionApi.commitApplicationVersion(ID, request);
        assertEquals("Committed application attributes", 201, responseCode);
    }

}
