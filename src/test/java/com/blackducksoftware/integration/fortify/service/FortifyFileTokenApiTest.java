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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.model.FileToken;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class FortifyFileTokenApiTest extends TestCase {
    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    @Override
    @Before
    public void setUp() {
        blackDuckFortifyJobConfig = new BlackDuckFortifyJobConfig();
    }

    @Test
    public void getFileToken() throws Exception {
        System.out.println("Executing getFileToken");
        FileToken fileToken = new FileToken("UPLOAD");

        String fileTokenResponse = blackDuckFortifyJobConfig.getFortifyFileTokenApi().getFileToken(fileToken);
        System.out.println("fileTokenResponse::" + fileTokenResponse);
        Assert.assertNotNull(fileTokenResponse);
    }

    @Test
    public void deleteFileToken() throws Exception {
        System.out.println("Executing deleteFileToken");
        int responseCode = blackDuckFortifyJobConfig.getFortifyFileTokenApi().deleteFileToken();
        System.out.println("Response code::" + responseCode);
    }

}
