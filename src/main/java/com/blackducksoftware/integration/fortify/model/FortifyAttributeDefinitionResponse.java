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

import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class FortifyAttributeDefinitionResponse {
    @SerializedName("data")
    private final List<FortifyAttributeDefinition> fortifyAttributeDefinitions;

    public FortifyAttributeDefinitionResponse(List<FortifyAttributeDefinition> fortifyAttributeDefinitions) {
        this.fortifyAttributeDefinitions = fortifyAttributeDefinitions;
    }

    public final class FortifyAttributeDefinition {
        private final Integer id;

        private final String name;

        private final String category;

        private final String type;

        private final boolean required;

        private final List<Option> options;

        public FortifyAttributeDefinition(Integer id, String name, String category, String type, boolean required, List<Option> options) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.type = type;
            this.required = required;
            this.options = options;
        }

        public final class Option {
            private final String guid;

            public Option(String guid) {
                this.guid = guid;
            }

            public String getGuid() {
                return guid;
            }

            @Override
            public String toString() {
                return "Option [guid=" + guid + "]";
            }

        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getType() {
            return type;
        }

        public boolean isRequired() {
            return required;
        }

        public List<Option> getOptions() {
            return options;
        }

        @Override
        public String toString() {
            return "FortifyAttributeDefinition [id=" + id + ", name=" + name + ", category=" + category + ", type=" + type + ", required=" + required
                    + ", options=" + options + "]";
        }

    }

    public List<FortifyAttributeDefinition> getApplicationAttributeDefinitions() {
        return fortifyAttributeDefinitions;
    }

    @Override
    public String toString() {
        return "FortifyAttributeDefinitionResponse [fortifyAttributeDefinitions=" + fortifyAttributeDefinitions + "]";
    }
}
