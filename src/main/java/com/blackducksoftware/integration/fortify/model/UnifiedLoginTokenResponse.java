/*
Copyright (C) 2020 Synopsys, Inc.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.blackducksoftware.integration.fortify.model;

import java.util.Date;

/**
 * This class is used to store the Fortify Unified Login Token response data
 *
 * @author smanikantan
 *
 */
public final class UnifiedLoginTokenResponse {
    private final Data data;

    private final int responseCode;

    public UnifiedLoginTokenResponse(final Data data, final int responseCode) {
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
        private final int id;
        private final String token;
        private final Date creationDate;
        private final Date terminalDate;
        private final int remainingUsages;
        private final String type;
        private final String description;
        private final String username;
        private final String _href;

        public Data(int id, String token, Date creationDate, Date terminalDate, int remainingUsages, String type,
                String description, String username, String _href) {
            super();
            this.id = id;
            this.token = token;
            this.creationDate = creationDate;
            this.terminalDate = terminalDate;
            this.remainingUsages = remainingUsages;
            this.type = type;
            this.description = description;
            this.username = username;
            this._href = _href;
        }

        public int getId() {
            return id;
        }

        public String getToken() {
            return token;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public Date getTerminalDate() {
            return terminalDate;
        }

        public int getRemainingUsages() {
            return remainingUsages;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getUsername() {
            return username;
        }

        public String get_href() {
            return _href;
        }

        @Override
        public String toString() {
            return "Data [id=" + id + ", token=" + token + ", creationDate=" + creationDate + ", terminalDate="
                    + terminalDate + ", remainingUsages=" + remainingUsages + ", type=" + type + ", description="
                    + description + ", username=" + username + ", _href=" + _href + "]";
        }

    }

}
