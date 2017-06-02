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

import org.junit.Assert;
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
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

@RunWith(PowerMockRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = TestApplication.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ FortifyService.class, OkHttpClient.class, OkHttpClient.Builder.class, FortifyFileTokenApi.class, FortifyUploadApi.class })
public class FortifyUploadApiTest extends TestCase {

    @Test
    public void uploadCSVFile() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");

        PowerMockito.mockStatic(OkHttpClient.Builder.class);
        OkHttpClient.Builder builder = Mockito.mock(OkHttpClient.Builder.class);

        PowerMockito.mockStatic(FortifyService.class);
        Mockito.when(FortifyService.getHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(builder);

        PowerMockito.mockStatic(OkHttpClient.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(builder.build()).thenReturn(okHttpClient);

        String token = "ABCDEFG";

        PowerMockito.mockStatic(FortifyFileTokenApi.class);
        Mockito.when(FortifyFileTokenApi.getFileToken(Mockito.any())).thenReturn(token);

        String fileTokenResponse = FortifyFileTokenApi.getFileToken(fileToken);
        System.out.println("File Token::" + fileTokenResponse);
        Assert.assertNotNull(token);

        // File file = new File("/Users/smanikantan/Downloads/security.csv");
        File file = Mockito.mock(File.class);
        // File file = new
        // File("/Users/smanikantan/Documents/hub-fortify-integration/report/solrWar2_4.10.4_20170510160506866.csv");
        System.out.println("file::" + file);

        JobStatusResponse mockUploadVulnerabilityResponseBody = new JobStatusResponse();
        mockUploadVulnerabilityResponseBody.setCode("-10001");
        mockUploadVulnerabilityResponseBody.setMessage("Background submission succeeded.");
        mockUploadVulnerabilityResponseBody.setId("JOB_ARTIFACTUPLOAD$f640bef6-703c-4287-926d-7032baed0d7a");
        mockUploadVulnerabilityResponseBody.setInvokingUserName("admin");
        mockUploadVulnerabilityResponseBody.setJobType(10);
        mockUploadVulnerabilityResponseBody.setJobState(0);

        PowerMockito.mockStatic(FortifyUploadApi.class);
        Mockito.when(FortifyUploadApi.uploadVulnerabilityByProjectVersion(Mockito.anyString(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(mockUploadVulnerabilityResponseBody);

        JobStatusResponse uploadVulnerabilityResponseBody = FortifyUploadApi.uploadVulnerabilityByProjectVersion(token, 2l, file);
        System.out.println("uploadVulnerabilityResponse::" + uploadVulnerabilityResponseBody);
        Assert.assertNotNull(uploadVulnerabilityResponseBody);
    }
}
