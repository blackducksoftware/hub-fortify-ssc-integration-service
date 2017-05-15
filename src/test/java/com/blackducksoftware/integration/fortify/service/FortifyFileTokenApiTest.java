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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
public class FortifyFileTokenApiTest extends TestCase {

    @Test
    public void getFileToken() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        FileTokenResponse fileTokenResponse = FortifyFileTokenApi.getFileToken(fileToken);
        System.out.println("fileTokenResponse::" + fileTokenResponse.getData().getToken());
    }

    @Test
    public void deleteFileToken() throws Exception {
        int responseCode = FortifyFileTokenApi.deleteFileToken();
        System.out.println("Response code::" + responseCode);
    }
}
