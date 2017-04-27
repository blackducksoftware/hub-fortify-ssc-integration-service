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
package com.blackducksoftware.integration.fortify.datamodel;

public class FileTokenResponse {
    private Data data;

    private String responseCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "Data::" + data.toString() + ", responseCode::" + responseCode;
    }

    public class Data {
        private String token;

        private String fileTokenType;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getFileTokenType() {
            return fileTokenType;
        }

        public void setFileTokenType(String fileTokenType) {
            this.fileTokenType = fileTokenType;
        }

        @Override
        public String toString() {
            return "token::" + token + ", fileTokenType::" + fileTokenType;
        }
    }
}
