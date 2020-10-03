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

package com.blackducksoftware.integration.fortify.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.job.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.util.AttributeConstants;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { BlackDuckFortifyJobConfig.class })
@ContextConfiguration(classes = { PropertyConstants.class, AttributeConstants.class, SpringConfiguration.class, BlackDuckFortifyJobConfig.class,
        BatchSchedulerConfig.class })
public class BlackDuckFortifyJobConfigTest extends TestCase {

    @Autowired
    private BlackDuckFortifyJobConfig blackduckFortifyJobConfig;

    @Test
    public void executeBatchJob() throws Exception {
        blackduckFortifyJobConfig.execute();
    }
}
