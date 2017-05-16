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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse.Data;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

@RunWith(PowerMockRunner.class)
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

        FileTokenResponse mockFileTokenResponse = new FileTokenResponse();
        Data data = mockFileTokenResponse.new Data();
        data.setToken("ABCDEFG");
        mockFileTokenResponse.setData(data);

        PowerMockito.mockStatic(FortifyFileTokenApi.class);
        Mockito.when(FortifyFileTokenApi.getFileToken(Mockito.any())).thenReturn(mockFileTokenResponse);

        FileTokenResponse fileTokenResponse = FortifyFileTokenApi.getFileToken(fileToken);
        String token = fileTokenResponse.getData().getToken();
        System.out.println("File Token::" + token);
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
