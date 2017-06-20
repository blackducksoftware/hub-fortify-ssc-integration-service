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

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to store the grouped mappings to handle many-to-one hub mappings with fortify application.
 *
 * @author hsathe
 *
 */
public class BlackDuckFortifyMapperGroup implements Serializable {
    private String fortifyApplication;

    private String fortifyApplicationVersion;

    private List<HubProjectVersion> hubProjectVersion;

    private int fortifyApplicationId;

    public String getFortifyApplication() {
        return fortifyApplication;
    }

    public void setFortifyApplication(String fortifyApplication) {
        this.fortifyApplication = fortifyApplication;
    }

    public String getFortifyApplicationVersion() {
        return fortifyApplicationVersion;
    }

    public void setFortifyApplicationVersion(String fortifyApplicationVersion) {
        this.fortifyApplicationVersion = fortifyApplicationVersion;
    }

    public int getFortifyApplicationId() {
        return fortifyApplicationId;
    }

    public void setFortifyApplicationId(int fortifyApplicationId) {
        this.fortifyApplicationId = fortifyApplicationId;
    }

    public List<HubProjectVersion> getHubProjectVersion() {
        return hubProjectVersion;
    }

    public void setHubProjectVersion(List<HubProjectVersion> hubProjectVersion) {
        this.hubProjectVersion = hubProjectVersion;
    }

    @Override
    public String toString() {
        return "BlackDuckFortifyMapperGroup [fortifyApplication=" + fortifyApplication + ", fortifyApplicationVersion=" + fortifyApplicationVersion
                + ", hubProjectVersion=" + hubProjectVersion + ", fortifyApplicationId=" + fortifyApplicationId + "]";
    }

}
