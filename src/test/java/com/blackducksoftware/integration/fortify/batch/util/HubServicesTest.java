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
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.Application;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class HubServicesTest extends TestCase {
    private final String PROJECT_NAME = "solrWar2";

    private final String VERSION_NAME = "4.10.4";

    @Autowired
    private HubServices hubServices;

    @Test
    public void getAllProjects() {
        System.out.println("Executing getAllProjects");
        List<ProjectView> projects = null;
        try {
            projects = hubServices.getAllProjects();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        assertNotNull(projects);
    }

    @Test
    public void getProjectVersionsByProject() {
        System.out.println("Executing getProjectVersionsByProject");
        ProjectView project = null;
        List<ProjectVersionView> projectVersionViews = new ArrayList<>();
        try {
            project = hubServices.getProjectByProjectName(PROJECT_NAME);
            projectVersionViews = hubServices.getProjectVersionsByProject(project);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        assertTrue(!projectVersionViews.isEmpty());
    }

    @Test
    public void getProjectVersion() {
        System.out.println("Executing getProjectVersion");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        assertNotNull(projectVersionItem);
    }

    @Test
    public void getProjectVersionWithInvalidProjectName() {
        System.out.println("Executing getProjectVersionWithInvalidProjectName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion("Solr1", VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("This Project does not exist"));
        }
        assertNull(projectVersionItem);
    }

    @Test
    public void getProjectVersionWithInvalidVersionName() {
        System.out.println("Executing getProjectVersionWithInvalidVersionName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, "3.10");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("Could not find the version"));
        }
        assertNull(projectVersionItem);
    }

    @Test
    public void getVulnerability() throws Exception {
        System.out.println("Executing getVulnerability");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        List<VulnerableComponentView> vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size() + ", vulnerableComponentViews::" + vulnerableComponentViews);
        assertNotNull(vulnerableComponentViews);
    }

    @Test
    public void getBomLastUpdatedAt() throws IllegalArgumentException, IntegrationException {
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        Date bomLastUpdatedAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
        System.out.println("bomLastUpdatedAt::" + bomLastUpdatedAt);
        assertNotNull(bomLastUpdatedAt);
    }
}
