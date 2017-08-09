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
package com.blackducksoftware.integration.fortify.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.RestConnectionHelper;
import com.blackducksoftware.integration.fortify.service.FortifyApplicationVersionApi;
import com.blackducksoftware.integration.fortify.service.FortifyAttributeDefinitionApi;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;

@Configuration
@ComponentScan(value = "com.blackducksoftware.integration.hub.fod.batch")
public class SpringConfiguration {
    @Bean
    public FortifyApplicationVersionApi getFortifyApplicationVersionApi() {
        return new FortifyApplicationVersionApi();
    }

    @Bean
    public FortifyAttributeDefinitionApi getFortifyAttributeDefinitionApi() {
        return new FortifyAttributeDefinitionApi();
    }

    @Bean
    public FortifyFileTokenApi getFortifyFileTokenApi() {
        return new FortifyFileTokenApi();
    }

    @Bean
    public FortifyUploadApi getFortifyUploadApi() {
        return new FortifyUploadApi();
    }

    @Bean
    public HubServices getHubServices() {
        return new HubServices(RestConnectionHelper.createHubServicesFactory());
    }

    @Bean
    public MappingParser getMappingParser() {
        return new MappingParser();
    }

}
