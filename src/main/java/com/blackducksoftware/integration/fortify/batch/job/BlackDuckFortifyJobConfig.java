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
import java.util.List;

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
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.processor.BlackDuckFortifyProcessor;
import com.blackducksoftware.integration.fortify.batch.reader.BlackDuckReader;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.writer.FortifyWriter;

@Configuration
@EnableBatchProcessing
public class BlackDuckFortifyJobConfig implements JobExecutionListener {

    @Autowired
    private BatchSchedulerConfig batchScheduler;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MappingParser parser;

    private final String MAPPING_FILE = "src/main/resources/mapping.json";

    @Bean
    public BlackDuckReader getBlackduckScanReader() {
        return new BlackDuckReader();
    }

    @Bean
    public BlackDuckFortifyProcessor getBlackduckFortifyProcessor() {
        return new BlackDuckFortifyProcessor();
    }

    @Bean
    public FortifyWriter getFortifyPushWriter() {
        return new FortifyWriter();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(5);
        return asyncTaskExecutor;
    }

    @Scheduled(cron = "${cron.expressions}")
    public void execute() throws Exception {

        System.out.println("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = batchScheduler.jobLauncher().run(pushBlackDuckScanToFortifyJob(), param);

        System.out.println("Job finished with status :" + execution.getStatus());
    }

    @Bean
    public Job pushBlackDuckScanToFortifyJob() {
        return jobBuilderFactory.get("Push Blackduck Scan data to Fortify Job")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .flow(pushBlackDuckScanToFortifyStep()).end().build();
    }

    @Bean
    public Step pushBlackDuckScanToFortifyStep() {
        return stepBuilderFactory.get("Extract Latest Scan from Blackduck -> Transform -> Push Data To Fortify")
                .<List<VulnerableComponentView>, List<Vulnerability>> chunk(10000)
                .reader(getBlackduckScanReader())
                .processor(getBlackduckFortifyProcessor())
                .writer(getFortifyPushWriter())
                .taskExecutor(taskExecutor()).build();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Completed Job::" + new Date());
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Start Job::" + new Date());
        final List<BlackDuckFortifyMapper> blackDuckFortifyMappers = parser.createMapping(MAPPING_FILE);
        System.out.println("blackDuckFortifyMappers :" + blackDuckFortifyMappers.toString());
        jobExecution.getExecutionContext().put("BlackDuckFortifyMapper", blackDuckFortifyMappers);
    }
}
