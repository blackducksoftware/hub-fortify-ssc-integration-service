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
 *
 * MappingParser Tests using Mockito
 *
 * @author hsathe
 *
 */
package com.blackducksoftware.integration.fortify.batch.util;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class MappingParserTest extends TestCase {

    @Test
    public void testMappingFileParser() throws Exception {
        List<BlackDuckFortifyMapper> mapping;
        try {
            mapping = MappingParser.createMapping(PropertyConstants.getMappingJsonPath());
            System.out.println(mapping);
        } catch (Exception e) {
            e.printStackTrace();
            // Logger info to be added
        }
    }
}
