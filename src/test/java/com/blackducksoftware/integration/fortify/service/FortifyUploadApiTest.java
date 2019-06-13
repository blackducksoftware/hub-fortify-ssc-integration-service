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
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.job.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FileToken;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(classes = { SpringConfiguration.class, BlackDuckFortifyJobConfig.class, BatchSchedulerConfig.class, PropertyConstants.class })
public class FortifyUploadApiTest extends TestCase {

    @Autowired
    private FortifyUploadApi fortifyUploadApi;

    @Autowired
    private FortifyFileTokenApi fortifyFileTokenApi;

    @Test
    public void uploadCSVFile() throws Exception {
        int id = 0;
        try {

            System.out.println("Executing uploadCSVFile");
            FileToken fileToken = new FileToken("UPLOAD");

            String fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
            System.out.println("File Token::" + fileTokenResponse);
            Assert.assertNotNull(fileTokenResponse);

            File file = new File("sample.csv");
            // File file = Mockito.mock(File.class);
            // File file = new
            // File("/Users/smanikantan/Documents/hub-fortify-integration/report/solrWar2_4.10.4_20170510160506866.csv");
            System.out.println("file::" + file);

            boolean response = fortifyUploadApi.uploadVulnerabilityByProjectVersion(fileTokenResponse, id, file);
            System.out.println("uploadVulnerabilityResponse::" + response);
            Assert.assertTrue(response);
        } catch (IOException e) {
            throw new IOException(e);
        } catch (IntegrationException e) {
            throw new IntegrationException(e);
        }
    }
}
