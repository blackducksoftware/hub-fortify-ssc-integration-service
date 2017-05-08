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

public class BlackDuckFortifyMapper {
    private String hubProject;

    private String hubProjectVersion;

    private String fortifyApplication;

    private String fortifyApplicationVersion;

    private int fortifyApplicationId;

    public String getHubProject() {
        return hubProject;
    }

    public void setHubProject(String hubProject) {
        this.hubProject = hubProject;
    }

    public String getHubProjectVersion() {
        return hubProjectVersion;
    }

    public void setHubProjectVersion(String hubProjectVersion) {
        this.hubProjectVersion = hubProjectVersion;
    }

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

    @Override
    public String toString() {
        return "BlackDuckFortifyMapper [hubProject=" + hubProject + ", hubProjectVersion=" + hubProjectVersion + ", fortifyApplication=" + fortifyApplication
                + ", fortifyApplicationVersion=" + fortifyApplicationVersion + ", fortifyApplicationId=" + fortifyApplicationId + "]";
    }

}
