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
package com.blackducksoftware.integration.fortify.batch.model;

import com.blackducksoftware.integration.hub.model.HubView;
import com.google.gson.annotations.SerializedName;

/**
 * This class will store the Vulnerability BOM component API response
 * 
 * @author smanikantan
 *
 */
public class VulnerableComponentView extends HubView {
    private String componentName;

    private String componentVersionName;

    private String componentVersionOriginName;

    private String componentVersionOriginId;

    @SerializedName("componentVersion")
    private String componentVersionLink;

    private VulnerabilityWithRemediationView vulnerabilityWithRemediation;

    public String getComponentName() {
        return this.componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentVersionName() {
        return this.componentVersionName;
    }

    public void setComponentVersionName(String componentVersionName) {
        this.componentVersionName = componentVersionName;
    }

    public String getComponentVersionOriginName() {
        return componentVersionOriginName;
    }

    public void setComponentVersionOriginName(String componentVersionOriginName) {
        this.componentVersionOriginName = componentVersionOriginName;
    }

    public String getComponentVersionOriginId() {
        return componentVersionOriginId;
    }

    public void setComponentVersionOriginId(String componentVersionOriginId) {
        this.componentVersionOriginId = componentVersionOriginId;
    }

    public String getComponentVersionLink() {
        return this.componentVersionLink;
    }

    public void setComponentVersionLink(String componentVersionLink) {
        this.componentVersionLink = componentVersionLink;
    }

    public VulnerabilityWithRemediationView getVulnerabilityWithRemediation() {
        return this.vulnerabilityWithRemediation;
    }

    public void setVulnerabilityWithRemediation(VulnerabilityWithRemediationView vulnerabilityWithRemediation) {
        this.vulnerabilityWithRemediation = vulnerabilityWithRemediation;
    }
}
