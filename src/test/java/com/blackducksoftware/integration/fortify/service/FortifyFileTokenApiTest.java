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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.Application;
import com.blackducksoftware.integration.fortify.datamodel.FileToken;
import com.blackducksoftware.integration.fortify.datamodel.FileTokenResponse;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class FortifyFileTokenApiTest extends TestCase {
    @Autowired
    private FortifyFileTokenApi fortifyFileTokenApi;

    @Test
    public void getFileToken() throws Exception {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        fortifyFileTokenApi.init();
        FileTokenResponse fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
        System.out.println("fileTokenResponse::" + fileTokenResponse.getData().getToken());
    }
}
