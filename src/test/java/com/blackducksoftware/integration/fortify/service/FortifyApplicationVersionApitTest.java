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

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
public class FortifyApplicationVersionApitTest extends TestCase {

    String FIELDS = "id";

    String QUERY = "name:1.3+and+project.name:Logistics";

    @Test
    public void getApplicationVersionTest() throws IOException {
        FortifyApplicationResponse response = FortifyApplicationVersionApi.getApplicationByName(FIELDS, QUERY);
        System.out.println("ID: " + response.getData().get(0).getId());
        System.out.println(response.toString());
    }

}
