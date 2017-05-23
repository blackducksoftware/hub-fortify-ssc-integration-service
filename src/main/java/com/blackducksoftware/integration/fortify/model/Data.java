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
 *
 * Holder for Gson conversions from Fortify REST API json response to objects.
 *
 * @author hsathe
 *
 */
package com.blackducksoftware.integration.fortify.model;

public class Data {
    private Integer id;

    private String name;

    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Data [id=" + id + "]";

        // return "Data [id=" + id + ", name=" + name + ", project=" + project + "]";
    }

    public class Project {

        private int id;

        private String name;

        private String description;

        private String issueTemplateId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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
