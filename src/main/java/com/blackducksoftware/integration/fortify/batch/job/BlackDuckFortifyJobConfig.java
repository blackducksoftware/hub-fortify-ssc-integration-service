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
package com.blackducksoftware.integration.fortify.batch.job;

import java.util.Date;

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
import com.blackducksoftware.integration.fortify.batch.step.Initializer;

@Configuration
@EnableBatchProcessing
public class BlackDuckFortifyJobConfig implements JobExecutionListener {

    @Autowired
    private BatchSchedulerConfig batchScheduler;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Initializer getMappingParserTask() {
        return new Initializer();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(2);
        return asyncTaskExecutor;
    }

    @Scheduled(cron = "${cron.expressions}")
    public void execute() throws Exception {
        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();
        batchScheduler.jobLauncher().run(pushBlackDuckScanToFortifyJob(), param);
    }

    @Bean
    public Job pushBlackDuckScanToFortifyJob() {
        return jobBuilderFactory.get("Push Blackduck Scan data to Fortify Job")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .flow(createMappingParserStep())
                .end().build();
    }

    @Bean
    public Step createMappingParserStep() {
        return stepBuilderFactory.get("Parse the Mapping.json -> Transform to Mapping parser object")
                .tasklet(getMappingParserTask()).build();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Completed Job Time::" + new Date());
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Started Job Time::" + new Date());
    }
}
