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
