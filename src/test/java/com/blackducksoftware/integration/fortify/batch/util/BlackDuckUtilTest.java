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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.TestApplication;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class BlackDuckUtilTest extends TestCase {
    private String HUB_PROJECT_NAME_1;

    private String HUB_PROJECT_VERSION_NAME_1;

    private String HUB_PROJECT_NAME_2;

    private String HUB_PROJECT_VERSION_NAME_2;

    private List<Vulnerability> transformMapping(List<VulnerableComponentView> vulnerabilityComponentViews, String hubProjectName, String hubProjectVersion,
            Date scanDate) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        vulnerabilityComponentViews.forEach(vulnerableComponentView -> {
            Vulnerability vulnerability = new Vulnerability();
            vulnerability.setProjectName(String.valueOf(hubProjectName));
            vulnerability.setProjectVersion(String.valueOf(hubProjectVersion));
            String[] componentVersionLinkArr = vulnerableComponentView.getComponentVersionLink().split("/");
            vulnerability.setProjectId(String.valueOf(componentVersionLinkArr[5]));
            vulnerability.setVersionId(String.valueOf(componentVersionLinkArr[7]));
            vulnerability.setChannelVersionId("");
            vulnerability.setComponentName(String.valueOf(vulnerableComponentView.getComponentName()));
            vulnerability.setVersion(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setChannelVersionOrigin(String.valueOf(vulnerableComponentView.getComponentVersionOriginName()));
            vulnerability.setChannelVersionOriginId(String.valueOf(vulnerableComponentView.getComponentVersionOriginId()));
            vulnerability.setChannelVersionOriginName(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setVulnerabilityId(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()));
            vulnerability.setDescription(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getDescription().replaceAll("\\r\\n", "")));
            vulnerability.setPublishedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityPublishedDate());
            vulnerability.setUpdatedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityUpdatedDate());
            vulnerability.setBaseScore(vulnerableComponentView.getVulnerabilityWithRemediation().getBaseScore());
            vulnerability.setExploitability(vulnerableComponentView.getVulnerabilityWithRemediation().getExploitabilitySubscore());
            vulnerability.setImpact(vulnerableComponentView.getVulnerabilityWithRemediation().getImpactSubscore());
            vulnerability.setVulnerabilitySource(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSource()));
            vulnerability.setHubVulnerabilityUrl(PropertyConstants.getHubServerUrl() + "/ui/vulnerabilities/id:"
                    + String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()) + "/view:overview");
            vulnerability.setRemediationStatus(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationStatus()));
            vulnerability.setRemediationTargetDate(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationTargetAt());
            vulnerability.setRemediationActualDate(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationActualAt());
            vulnerability.setRemediationComment(String.valueOf(""));
            vulnerability.setUrl("NVD".equalsIgnoreCase(vulnerableComponentView.getVulnerabilityWithRemediation().getSource())
                    ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()
                    : "");
            vulnerability.setSeverity(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSeverity()));
            vulnerability.setScanDate(scanDate);
            vulnerabilities.add(vulnerability);
        });
        return vulnerabilities;
    }

    @Override
    @Before
    public void setUp() {
        System.out.println("path::" + PropertyConstants.getMappingJsonPath());
        final List<BlackDuckFortifyMapperGroup> blackDuckFortifyMappers = MappingParser
                .createMapping(PropertyConstants.getMappingJsonPath());
        HUB_PROJECT_NAME_1 = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProject();
        HUB_PROJECT_VERSION_NAME_1 = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProjectVersion();
        HUB_PROJECT_NAME_2 = blackDuckFortifyMappers.get(1).getHubProjectVersion().get(0).getHubProject();
        HUB_PROJECT_VERSION_NAME_2 = blackDuckFortifyMappers.get(1).getHubProjectVersion().get(0).getHubProjectVersion();
    }

    @Test
    public void testRemoveDuplicates() {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        try {
            ProjectVersionView projectVersionItem1 = HubServices.getProjectVersion(HUB_PROJECT_NAME_1, HUB_PROJECT_VERSION_NAME_1);
            ProjectVersionView projectVersionItem2 = HubServices.getProjectVersion(HUB_PROJECT_NAME_2, HUB_PROJECT_VERSION_NAME_2);
            vulnerabilities.addAll(transformMapping(HubServices.getVulnerabilityComponentViews(projectVersionItem1), HUB_PROJECT_NAME_1,
                    HUB_PROJECT_VERSION_NAME_1, new Date()));
            vulnerabilities.addAll(transformMapping(HubServices.getVulnerabilityComponentViews(projectVersionItem2), HUB_PROJECT_NAME_2,
                    HUB_PROJECT_VERSION_NAME_2, new Date()));

            vulnerabilities = BlackDuckUtil.removeDuplicates(vulnerabilities);

            assertNotNull(vulnerabilities);
            System.out.println("vulnerabilities count::" + vulnerabilities.size() + ", vulnerabilities::" + vulnerabilities);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
    }
}
