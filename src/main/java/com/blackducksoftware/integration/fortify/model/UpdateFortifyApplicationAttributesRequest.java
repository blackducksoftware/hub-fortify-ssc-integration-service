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
