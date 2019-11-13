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

import java.util.Date;

import com.blackducksoftware.integration.hub.api.core.HubView;
import com.google.gson.annotations.SerializedName;

/**
 * @author manikan
 *
 */
public final class Remediating extends HubView {
    @SerializedName("componentVersion")
    private final String componentVersionLink;

    private final String name;

    private final Date releasedOn;

    private final int vulnerabilityCount;

    /**
     * @param componentVersionLink
     * @param name
     * @param releasedOn
     * @param vulnerabilityCount
     */
    public Remediating(final String componentVersionLink, final String name, final Date releasedOn, final int vulnerabilityCount) {
        this.componentVersionLink = componentVersionLink;
        this.name = name;
        this.releasedOn = releasedOn;
        this.vulnerabilityCount = vulnerabilityCount;
    }

    /**
     * @return the componentVersionLink
     */
    public String getComponentVersionLink() {
        return componentVersionLink;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the releasedOn
     */
    public Date getReleasedOn() {
        return releasedOn;
    }

    /**
     * @return the vulnerabilityCount
     */
    public int getVulnerabilityCount() {
        return vulnerabilityCount;
    }

    @Override
    public String toString() {
        return "Remediating [" + (componentVersionLink != null ? "componentVersionLink=" + componentVersionLink + ", " : "")
                + (name != null ? "name=" + name + ", " : "") + (releasedOn != null ? "releasedOn=" + releasedOn + ", " : "") + "vulnerabilityCount="
                + vulnerabilityCount + "]";
    }

}
