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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.Application;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;

import junit.framework.TestCase;
import okhttp3.ResponseBody;
import retrofit2.Call;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class FortifyUploadApiTest extends TestCase {

    @Autowired
    private FortifyFileTokenApi fortifyFileTokenApi;

    @Autowired
    private FortifyUploadApi fortifyUploadApi;

    @Test
    public void uploadCSVFile() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        FileTokenResponse fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
        String token = fileTokenResponse.getData().getToken();
        System.out.println("File Token::" + token);
        File file = new File("/Users/smanikantan/Downloads/security.zip");
        Call<ResponseBody> uploadVulnerabilityResponse = fortifyUploadApi.uploadVulnerabilityByProjectVersion(token, 2l, file);
        System.out.println("uploadVulnerabilityResponse::" + uploadVulnerabilityResponse.execute().body());
    }
}
