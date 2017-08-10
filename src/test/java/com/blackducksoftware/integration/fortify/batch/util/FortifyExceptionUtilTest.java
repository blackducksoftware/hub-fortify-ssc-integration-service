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
package com.blackducksoftware.integration.fortify.batch.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.TestApplication;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class FortifyExceptionUtilTest extends TestCase {
    @Test
    public void verifyCustomException() {
        System.out.println("Executing verifyCustomException");
        try {
            FortifyExceptionUtil.verifyFortifyResponseCode(200, "Create Fortify Application Version API");
            FortifyExceptionUtil.verifyFortifyResponseCode(201, "Update Fortify Application Version API");
            try {
                FortifyExceptionUtil.verifyFortifyResponseCode(400, "Create Fortify Application Version API");
            } catch (IntegrationException e) {
                // do nothing
            }
            try {
                FortifyExceptionUtil.verifyFortifyResponseCode(403, "Create Fortify Application Version API");
            } catch (IntegrationException e) {
                // do nothing
            }
            try {
                FortifyExceptionUtil.verifyFortifyResponseCode(404, "Create Fortify Application Version API");
            } catch (IntegrationException e) {
                // do nothing
            }
            try {
                FortifyExceptionUtil.verifyFortifyResponseCode(500, "Create Fortify Application Version API");
            } catch (IntegrationException e) {
                // do nothing
            }
        } catch (IntegrationException e) {
            // do nothing
        }
    }

    @Test
    public void throwFortifyCustomException() {
        System.out.println("Executing throwFortifyCustomException");
        try {
            FortifyExceptionUtil.throwFortifyCustomException(-10003, "Fortify Upload Api", "Error while submitting the request");
        } catch (IntegrationException e) {
            // do nothing
        }
    }
}
