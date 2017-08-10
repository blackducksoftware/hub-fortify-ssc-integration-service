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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class FortifyUploadApiTest extends TestCase {
    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    @Override
    @Before
    public void setUp() {
        blackDuckFortifyJobConfig = new BlackDuckFortifyJobConfig();
    }

    @Test
    public void uploadCSVFile() throws Exception {
        System.out.println("Executing uploadCSVFile");
        FileToken fileToken = new FileToken("UPLOAD");

        String fileTokenResponse = blackDuckFortifyJobConfig.getFortifyFileTokenApi().getFileToken(fileToken);
        System.out.println("File Token::" + fileTokenResponse);
        Assert.assertNotNull(fileTokenResponse);

        File file = new File("sample.csv");
        // File file = Mockito.mock(File.class);
        // File file = new
        // File("/Users/smanikantan/Documents/hub-fortify-integration/report/solrWar2_4.10.4_20170510160506866.csv");
        System.out.println("file::" + file);

        JobStatusResponse uploadVulnerabilityResponseBody = blackDuckFortifyJobConfig.getFortifyUploadApi().uploadVulnerabilityByProjectVersion(
                fileTokenResponse, 2l,
                file);
        System.out.println("uploadVulnerabilityResponse::" + uploadVulnerabilityResponseBody);
        Assert.assertNotNull(uploadVulnerabilityResponseBody);
    }
}
