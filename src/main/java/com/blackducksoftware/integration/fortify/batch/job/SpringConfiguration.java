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
package com.blackducksoftware.integration.fortify.batch.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.batch.util.RestConnectionHelper;

@Configuration
public class SpringConfiguration {
    /**
     * Created the bean to get the instance of Hub Services
     *
     * @return
     */
    @Bean
    public HubServices getHubServices(final PropertyConstants propertyConstants) {
        return new HubServices(RestConnectionHelper.createHubServicesFactory(propertyConstants));
    }

    /**
     * Create the task executor which will be used for multi-threading
     *
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        final SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(SimpleAsyncTaskExecutor.NO_CONCURRENCY);
        return asyncTaskExecutor;
    }
}
