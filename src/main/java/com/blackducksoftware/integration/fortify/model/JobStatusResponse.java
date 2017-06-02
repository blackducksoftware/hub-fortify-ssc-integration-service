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

import org.simpleframework.xml.Element;

/**
 * This class is used to store the Fortify upload response data
 *
 * @author smanikantan
 *
 */
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
