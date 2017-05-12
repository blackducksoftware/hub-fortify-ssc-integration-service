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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
public class FortifyUploadApiTest extends TestCase {

    @Test
    public void uploadCSVFile() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        FileTokenResponse fileTokenResponse = FortifyFileTokenApi.getFileToken(fileToken);
        String token = fileTokenResponse.getData().getToken();
        System.out.println("File Token::" + token);

        // File file = new File("/Users/smanikantan/Downloads/security.csv");
        File file = new File("/Users/smanikantan/Documents/hub-fortify-integration/report/solrWar2_4.10.4_20170510160506866.csv");
        System.out.println("file::" + file);
        JobStatusResponse uploadVulnerabilityResponseBody = FortifyUploadApi.uploadVulnerabilityByProjectVersion(token, 2l, file);
        System.out.println("uploadVulnerabilityResponse::" + uploadVulnerabilityResponseBody);
        Assert.assertNotNull(uploadVulnerabilityResponseBody);
    }
}
