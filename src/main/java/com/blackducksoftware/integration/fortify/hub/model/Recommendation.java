/*
 * Copyright (C) 2019 Black Duck Software Inc.
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
package com.blackducksoftware.integration.fortify.hub.model;

import com.blackducksoftware.integration.hub.api.core.HubView;

/**
 * @author manikan
 *
 */
public final class Recommendation extends HubView {

    private final Remediating fixesPreviousVulnerabilities;

    private final Remediating noVulnerabilities;

    private final Remediating latestAfterCurrent;

    /**
     * @param fixesPreviousVulnerabilities
     * @param noVulnerabilities
     * @param latestAfterCurrent
     */
    public Recommendation(final Remediating fixesPreviousVulnerabilities, final Remediating noVulnerabilities, final Remediating latestAfterCurrent) {
        this.fixesPreviousVulnerabilities = fixesPreviousVulnerabilities;
        this.noVulnerabilities = noVulnerabilities;
        this.latestAfterCurrent = latestAfterCurrent;
    }

    /**
     * @return the fixesPreviousVulnerabilities
     */
    public Remediating getFixesPreviousVulnerabilities() {
        return fixesPreviousVulnerabilities;
    }

    /**
     * @return the noVulnerabilities
     */
    public Remediating getNoVulnerabilities() {
        return noVulnerabilities;
    }

    /**
     * @return the latestAfterCurrent
     */
    public Remediating getLatestAfterCurrent() {
        return latestAfterCurrent;
    }

    @Override
    public String toString() {
        return "Recommendation [" + (fixesPreviousVulnerabilities != null ? "fixesPreviousVulnerabilities=" + fixesPreviousVulnerabilities + ", " : "")
                + (noVulnerabilities != null ? "noVulnerabilities=" + noVulnerabilities + ", " : "")
                + (latestAfterCurrent != null ? "latestAfterCurrent=" + latestAfterCurrent : "") + "]";
    }

}
