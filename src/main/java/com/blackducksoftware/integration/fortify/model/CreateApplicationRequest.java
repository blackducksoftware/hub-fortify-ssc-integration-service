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
package com.blackducksoftware.integration.fortify.model;

import java.io.Serializable;

public final class CreateApplicationRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String name;

    private final String description;

    private final Boolean active;

    private final Boolean committed;

    private final Project project;

    private final String issueTemplateId;

    public CreateApplicationRequest(final String name, final String description, final Boolean active, final Boolean committed, final Project project,
            final String issueTemplateId) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.committed = committed;
        this.project = project;
        this.issueTemplateId = issueTemplateId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getCommitted() {
        return committed;
    }

    public Project getProject() {
        return project;
    }

    public String getIssueTemplateId() {
        return issueTemplateId;
    }

    public final static class Project {

        private final String id;

        private final String name;

        private final String description;

        private final String issueTemplateId;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIssueTemplateId() {
            return issueTemplateId;
        }

        public Project(final String id, final String name, final String description, final String issueTemplateId) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.issueTemplateId = issueTemplateId;
        }

    }
}
