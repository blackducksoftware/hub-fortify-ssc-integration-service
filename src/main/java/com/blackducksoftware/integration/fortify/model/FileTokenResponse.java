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
 * This class is used to store the Fortify File Token response data
 *
 * @author smanikantan
 *
 */
public final class FileTokenResponse {
    private final Data data;

    private final int responseCode;

    public FileTokenResponse(final Data data, final int responseCode) {
        this.data = data;
        this.responseCode = responseCode;
    }

    public Data getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return "Data::" + data.toString() + ", responseCode::" + responseCode;
    }

    public final static class Data {
        private final String token;

        private final String fileTokenType;

        public Data(final String token, final String fileTokenType) {
            super();
            this.token = token;
            this.fileTokenType = fileTokenType;
        }

        public String getToken() {
            return token;
        }

        public String getFileTokenType() {
            return fileTokenType;
        }

        @Override
        public String toString() {
            return "token::" + token + ", fileTokenType::" + fileTokenType;
        }
    }
}
