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

/**
 * This class is to store the mapping details after parsing the mapping.json file
 *
 * @author hsathe
 *
 */
public final class BlackDuckFortifyMapper implements Serializable {
    private final String hubProject;

    private final String hubProjectVersion;

    private final String fortifyApplication;

    private final String fortifyApplicationVersion;

    public BlackDuckFortifyMapper(String hubProject, String hubProjectVersion, String fortifyApplication, String fortifyApplicationVersion) {
        this.hubProject = hubProject;
        this.hubProjectVersion = hubProjectVersion;
        this.fortifyApplication = fortifyApplication;
        this.fortifyApplicationVersion = fortifyApplicationVersion;
    }

    public String getHubProject() {
        return hubProject;
    }

    public String getHubProjectVersion() {
        return hubProjectVersion;
    }

    public String getFortifyApplication() {
        return fortifyApplication;
    }

    public String getFortifyApplicationVersion() {
        return fortifyApplicationVersion;
    }

    @Override
    public String toString() {
        return "BlackDuckFortifyMapper [hubProject=" + hubProject + ", hubProjectVersion=" + hubProjectVersion + ", fortifyApplication=" + fortifyApplication
                + ", fortifyApplicationVersion=" + fortifyApplicationVersion + "]";
    }
}
