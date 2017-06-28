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

/**
 * Holder for Gson conversions from Fortify REST API json response to objects.
 *
 * @author hsathe
 *
 */
public final class Data {
    private final Integer id;

    private final String name;

    private final Project project;

    public Data(Integer id, String name, Project project) {
        super();
        this.id = id;
        this.name = name;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Data [id=" + id + "]";
    }

    public final static class Project {

        private final int id;

        private final String name;

        private final String description;

        private final String issueTemplateId;

        public Project(int id, String name, String description, String issueTemplateId) {
            super();
            this.id = id;
            this.name = name;
            this.description = description;
            this.issueTemplateId = issueTemplateId;
        }

        public int getId() {
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
    }

}
