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
package com.blackducksoftware.integration.fortify.model;

import java.io.Serializable;

public class CreateApplicationRequest implements Serializable {

    private String name;

    private String description;

    private Boolean active;

    private Boolean committed;

    private Project project;

    private String issueTemplateId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getCommitted() {
        return committed;
    }

    public void setCommitted(Boolean committed) {
        this.committed = committed;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getIssueTemplateId() {
        return issueTemplateId;
    }

    public void setIssueTemplateId(String issueTemplateId) {
        this.issueTemplateId = issueTemplateId;
    }

    public class Project {

        private String name;

        private String description;

        private String issueTemplateId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIssueTemplateId() {
            return issueTemplateId;
        }

        public void setIssueTemplateId(String issueTemplateId) {
            this.issueTemplateId = issueTemplateId;
        }
    }
}
