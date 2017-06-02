/**
 * Copyright (C) 2017 Black Duck Software, Inc.
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
import java.util.List;

public class UpdateFortifyApplicationAttributesRequest implements Serializable {
    private Integer attributeDefinitionId;

    private List<Value> values = null;

    private Object value;

    public Integer getAttributeDefinitionId() {
        return attributeDefinitionId;
    }

    public void setAttributeDefinitionId(Integer attributeDefinitionId) {
        this.attributeDefinitionId = attributeDefinitionId;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public class Value {

        private String guid;

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        @Override
        public String toString() {
            return "Value [guid=" + guid + "]";
        }

    }

    @Override
    public String toString() {
        return "UpdateFortifyApplicationAttributesRequest [attributeDefinitionId=" + attributeDefinitionId + ", values=" + values + ", value=" + value
                + "]";
    }
}
