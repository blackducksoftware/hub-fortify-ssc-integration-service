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
package com.blackducksoftware.integration.fortify.batch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;

/**
 * This class will be used to have the common methods related to BlackDuck
 *
 * @author smanikantan
 *
 */
public final class BlackDuckUtil {

    /**
     * It will be used to remove the duplicate vulnerabilities in the list
     *
     * @param vulnerabilities
     * @return
     */
    public static List<Vulnerability> removeDuplicates(List<Vulnerability> vulnerabilities) {
        Map<String, Vulnerability> uniqueKeys = new HashMap<>();
        // Iterate the vulnerabilities to remove the duplicates
        vulnerabilities.forEach(vulnerability -> {
            // The unique vulnerability will be the combination of Component name and version, Channel version and
            // Vulnerability Id
            String uniqueKey = vulnerability.getComponentName() + "~" + vulnerability.getVersion() + "~" + vulnerability.getChannelVersionOriginId() + "~"
                    + vulnerability.getVulnerabilityId();

            // If the vulnerability is present in multiple project, then assign the project name and version name to
            // Multiple projects and Multiple versions respectively
            if (uniqueKeys.containsKey(uniqueKey)) {
                vulnerability.setProjectName("Multiple projects");
                vulnerability.setProjectVersion("Multiple versions");
            }
            uniqueKeys.put(uniqueKey, vulnerability);
        });
        return new ArrayList<>(uniqueKeys.values());
    }
}
