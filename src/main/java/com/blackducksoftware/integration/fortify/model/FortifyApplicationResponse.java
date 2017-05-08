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

import java.util.List;

public class FortifyApplicationResponse {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data {

        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Data [id=" + id + "]";
        }

    }

    @Override
    public String toString() {
        return "FortifyApplicationResponse [responseList=" + data + "]";
    }
}
