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

import org.simpleframework.xml.Element;

public class JobStatusResponse {
    @Element(name = "code")
    private String code;

    @Element(name = "msg")
    private String message;

    @Element(name = "id", required = false)
    private String id;

    @Element(name = "invokingUserName", required = false)
    private String invokingUserName;

    @Element(name = "jobType", required = false)
    private int jobType;

    @Element(name = "jobState", required = false)
    private int jobState;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvokingUserName() {
        return invokingUserName;
    }

    public void setInvokingUserName(String invokingUserName) {
        this.invokingUserName = invokingUserName;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public int getJobState() {
        return jobState;
    }

    public void setJobState(int jobState) {
        this.jobState = jobState;
    }

    @Override
    public String toString() {
        return "JobStatusResponse [code=" + code + ", message=" + message + ", id=" + id + ", invokingUserName=" + invokingUserName + ", jobType=" + jobType
                + ", jobState=" + jobState + "]";
    }
}
