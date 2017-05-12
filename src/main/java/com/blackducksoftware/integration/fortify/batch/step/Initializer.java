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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;

public class Initializer implements Tasklet, StepExecutionListener {

    private final MappingParser parser = new MappingParser();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Started MappingParserTask");

        Arrays.stream(new File(PropertyConstants.getProperty("hub.fortify.report.dir")).listFiles()).forEach(File::delete);
        System.out.println("Found Mapping file:: " + PropertyConstants.getProperty("hub.fortify.mapping.file.path"));
        final List<BlackDuckFortifyMapper> blackDuckFortifyMappers = parser.createMapping(PropertyConstants.getProperty("hub.fortify.mapping.file.path"));
        System.out.println("blackDuckFortifyMappers :" + blackDuckFortifyMappers.toString());

        ExecutorService exec = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>(blackDuckFortifyMappers.size());
        for (BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
            futures.add(exec.submit(new BlackDuckFortifyPushThread(blackDuckFortifyMapper)));
        }
        for (Future<?> f : futures) {
            f.get(); // wait for a processor to complete
        }

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
