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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import com.blackducksoftware.integration.fortify.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.fortify.batch.SpringConfiguration;
import com.blackducksoftware.integration.fortify.batch.step.Initializer;

/**
 * Schedule the batch job
 *
 * @author smanikantan
 *
 */
@Configuration
@EnableBatchProcessing
public class BlackDuckFortifyJobConfig implements JobExecutionListener {
    private final static Logger logger = Logger.getLogger(BlackDuckFortifyJobConfig.class);

    @Autowired
    private BatchSchedulerConfig batchScheduler;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Create bean for Spring Configuration
     *
     * @return Initializer
     */
    @Bean
    public SpringConfiguration getSpringConfiguration() {
        return new SpringConfiguration();
    }

    /**
     * Create new Initializer task
     *
     * @return Initializer
     */
    @Bean
    public Initializer getMappingParserTask() {
        return new Initializer(getSpringConfiguration());
    }

    /**
     * Create the task executor which will be used for multi-threading
     *
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(1);
        return asyncTaskExecutor;
    }

    /**
     * Schedule the job and add it to the job launcher
     *
     * @throws Exception
     */
    @Scheduled(cron = "${cron.expressions}")
    public void execute() throws Exception {
        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();
        batchScheduler.jobLauncher().run(pushBlackDuckScanToFortifyJob(), param);
    }

    /**
     * Create the job to push the vulnerability data from BlackDuck to Fortify Job
     *
     * @return Job
     */
    @Bean
    public Job pushBlackDuckScanToFortifyJob() {
        logger.info("Push Blackduck Scan data to Fortify Job");
        return jobBuilderFactory.get("Push Blackduck Scan data to Fortify Job")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .flow(createMappingParserStep())
                .end().build();
    }

    /**
     * Add the Mapping parser task to the job
     *
     * @return Step
     */
    @Bean
    public Step createMappingParserStep() {
        logger.info("Parse the Mapping.json -> Transform to Mapping parser object");
        return stepBuilderFactory.get("Parse the Mapping.json -> Transform to Mapping parser object")
                .tasklet(getMappingParserTask()).build();
    }

    /**
     * This function will execute after each job is completed
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job completed at::" + new Date());
    }

    /**
     * This function will execute before each job is started
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job started at::" + new Date());
    }
}
