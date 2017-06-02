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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse.Data;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = TestApplication.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ FortifyService.class, OkHttpClient.class, OkHttpClient.Builder.class, FortifyFileTokenApi.class })
public class FortifyFileTokenApiTest extends TestCase {

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

        PowerMockito.mockStatic(FortifyFileTokenApi.class);
    }

    @Test
    public void getFileToken() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");

        String token = "ABCDEFG";

        Mockito.when(FortifyFileTokenApi.getFileToken(Mockito.any())).thenReturn(token);

        String fileTokenResponse = FortifyFileTokenApi.getFileToken(fileToken);
        System.out.println("fileTokenResponse::" + fileTokenResponse);
        Assert.assertNotNull(fileTokenResponse);
    }

    @Test
    public void deleteFileToken() throws Exception {

        FileTokenResponse mockFileTokenResponse = new FileTokenResponse();
        Data data = mockFileTokenResponse.new Data();
        data.setToken("ABCDEFG");
        mockFileTokenResponse.setData(data);

        Mockito.when(FortifyFileTokenApi.deleteFileToken()).thenReturn(200);

        int responseCode = FortifyFileTokenApi.deleteFileToken();
        System.out.println("Response code::" + responseCode);
    }

}
