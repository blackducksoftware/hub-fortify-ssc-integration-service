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
package com.blackducksoftware.integration.fortify.batch.step;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;

@Configuration
public class MappingParserTask implements Tasklet, StepExecutionListener {

    @Autowired
    private MappingParser parser;

    private final String MAPPING_FILE = "src/main/resources/mapping.json";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Started MappingParserTask");
        final List<BlackDuckFortifyMapper> blackDuckFortifyMappers = parser.createMapping(MAPPING_FILE);
        ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        stepContext.put("blackDuckFortifyMapper", blackDuckFortifyMappers);
        System.out.println("blackDuckFortifyMappers :" + stepContext.get("blackDuckFortifyMapper"));
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
