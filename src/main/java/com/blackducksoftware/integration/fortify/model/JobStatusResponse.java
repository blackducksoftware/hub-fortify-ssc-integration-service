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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class is used to store the Fortify upload response data
 *
 * @author smanikantan
 *
 */
@Root
public final class JobStatusResponse implements Serializable {
    @Element(name = "code")
    private int code;

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

    public JobStatusResponse() {

    }

    public JobStatusResponse(int code, String message, String id, String invokingUserName, int jobType, int jobState) {
        this.code = code;
        this.message = message;
        this.id = id;
        this.invokingUserName = invokingUserName;
        this.jobType = jobType;
        this.jobState = jobState;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getInvokingUserName() {
        return invokingUserName;
    }

    public int getJobType() {
        return jobType;
    }

    public int getJobState() {
        return jobState;
    }

    @Override
    public String toString() {
        return "JobStatusResponse [code=" + code + ", message=" + message + ", id=" + id + ", invokingUserName=" + invokingUserName + ", jobType=" + jobType
                + ", jobState=" + jobState + "]";
    }
}
