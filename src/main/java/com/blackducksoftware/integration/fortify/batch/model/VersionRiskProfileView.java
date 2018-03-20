/*
 * Copyright (C) 2018 Black Duck Software Inc.
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
package com.blackducksoftware.integration.fortify.batch.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.api.core.HubResponse;
import com.blackducksoftware.integration.hub.api.core.LinkResponse;
import com.blackducksoftware.integration.hub.api.core.LinkSingleResponse;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;

public final class VersionRiskProfileView extends HubResponse {
    public static final Map<String, LinkResponse> links = new HashMap<>();

    public static final String VERSION_LINK = "version";

    public static final LinkSingleResponse<ProjectVersionView> VERSION_LINK_RESPONSE = new LinkSingleResponse<>(VERSION_LINK,
            ProjectVersionView.class);

    static {
        links.put(VERSION_LINK, VERSION_LINK_RESPONSE);
    }

    private final Date bomLastUpdatedAt;

    public VersionRiskProfileView(Date bomLastUpdatedAt) {
        this.bomLastUpdatedAt = bomLastUpdatedAt;
    }

    public Date getBomLastUpdatedAt() {
        return bomLastUpdatedAt;
    }

}
