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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.Application;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class HubUtilitiesTest extends TestCase {
    private final String PROJECT_NAME = "solrWar2";

    private final String VERSION_NAME = "4.10.4";

    @Autowired
    private HubUtilities hubUtilities;

    @Test
    public void getVulnerability() throws Exception {
        List<VulnerableComponentView> vulnerableComponentViews = hubUtilities.getVulnerabilityComponentViews(PROJECT_NAME, VERSION_NAME);
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size() + ", vulnerableComponentViews::" + vulnerableComponentViews);
        assertNotNull(vulnerableComponentViews);
    }

    @Test
    public void getVulnerabilityWithInvalidProjectName() {
        List<VulnerableComponentView> vulnerableComponentViews = new ArrayList<>();
        try {
            vulnerableComponentViews = hubUtilities.getVulnerabilityComponentViews("Solr1", VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("This Project does not exist"));
        }
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size() + ", vulnerableComponentViews::" + vulnerableComponentViews);
        assertTrue(vulnerableComponentViews.isEmpty());
    }

    @Test
    public void getVulnerabilityWithInvalidVersionName() {
        List<VulnerableComponentView> vulnerableComponentViews = new ArrayList<>();
        try {
            vulnerableComponentViews = hubUtilities.getVulnerabilityComponentViews(PROJECT_NAME, "3.10");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("Could not find the version"));
        }
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size() + ", vulnerableComponentViews::" + vulnerableComponentViews);
        assertTrue(vulnerableComponentViews.isEmpty());
    }
}
