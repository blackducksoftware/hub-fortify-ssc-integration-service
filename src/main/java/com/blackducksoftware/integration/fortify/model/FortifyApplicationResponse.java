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
 * Holder for Gson conversion from Fortify API json response to Java objects
 * 
 * @author hsathe
 *
 */
package com.blackducksoftware.integration.fortify.model;

import java.util.List;

public class FortifyApplicationResponse {
    private List<Data> data;

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FortifyApplicationResponse [responseList=" + data + "]";
    }
}
