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

import junit.framework.TestCase;
import okhttp3.OkHttpClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FortifyService.class, OkHttpClient.class, OkHttpClient.Builder.class, FortifyFileTokenApi.class })
public class FortifyFileTokenApiTest extends TestCase {

    @Test
    public void getFileToken() throws Exception {
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
        System.out.println("fileTokenResponse::" + fileTokenResponse.getData().getToken());
        Assert.assertNotNull(fileTokenResponse);
    }

    @Test
    public void deleteFileToken() throws Exception {
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
        Mockito.when(FortifyFileTokenApi.deleteFileToken()).thenReturn(200);

        int responseCode = FortifyFileTokenApi.deleteFileToken();
        System.out.println("Response code::" + responseCode);
    }

}
