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

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.job.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.service.FortifyUnifiedLoginTokenApi;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

/**
 * MappingParser Tests
 * 
 * @author manikan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MappingParser.class)
@ContextConfiguration(classes = { PropertyConstants.class, AttributeConstants.class, SpringConfiguration.class, BlackDuckFortifyJobConfig.class,
        BatchSchedulerConfig.class })
public class MappingParserTest extends TestCase {
    
    @Autowired
    private MappingParser mappingParser;

    @Autowired
    private PropertyConstants propertyConstants;
    
    @Autowired
    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;
    
    @Autowired
    private FortifyUnifiedLoginTokenApi fortifyUnifiedLoginTokenApi;

    @Test
    public void testMappingFileParser() throws JsonIOException, IOException, IntegrationException {
        System.out.println("Executing testMappingFileParser");
        List<BlackDuckFortifyMapperGroup> mapping = mappingParser.createMapping(propertyConstants.getMappingJsonPath());
        System.out.println("mapping: " + mapping);
        assertNotNull(mapping);
    }
    
    @Override
    @After
    public void tearDown() throws JsonIOException, IOException, IntegrationException {
        if (blackDuckFortifyJobConfig.getFortifyToken().getData() != null && blackDuckFortifyJobConfig.getFortifyToken().getData().getId() != 0) {
            fortifyUnifiedLoginTokenApi.deleteUnifiedLoginToken(blackDuckFortifyJobConfig.getFortifyToken().getData().getId());
        }
    }
}
