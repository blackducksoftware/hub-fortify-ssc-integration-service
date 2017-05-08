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
package com.blackducksoftware.integration.fortify.batch.util;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.Application;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class MappingParserTest extends TestCase {
    private final String mappingFilePath = "src/main/resources/mapping.json";

    @Autowired
    private MappingParser parser;

    @Test
    public void testMappingFileParser() throws Exception {
        // MappingParser parser;
        List<BlackDuckFortifyMapper> mapping;
        try {
            // parser = new MappingParser();
            mapping = parser.createMapping(mappingFilePath);
            System.out.println(mapping);
        } catch (Exception e) {
            e.printStackTrace();
            // Logger info to be added
        }
    }

    // @Test
    // public void addApplicationIdToResponseTest() {
    // // MappingParser parser;
    // List<BlackDuckFortifyMapper> mapping;
    // try {
    // // parser = new MappingParser();
    // mapping = parser.createMapping(mappingFilePath);
    // List<BlackDuckFortifyMapper> mappingObj = parser.addApplicationIdToResponse(mapping);
    // System.out.println(mappingObj);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

}
