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

/**
 * Holder for Gson conversion from Fortify API json response to Java objects
 *
 * @author hsathe
 *
 */
public final class FortifyApplicationResponse {
    private final List<Data> data;

    private final int responseCode;

    public FortifyApplicationResponse(final List<Data> data, final int responseCode) {
        this.data = data;
        this.responseCode = responseCode;
    }

    public List<Data> getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return "FortifyApplicationResponse [data=" + data.toString() + ", responseCode=" + responseCode + "]";
    }

}
