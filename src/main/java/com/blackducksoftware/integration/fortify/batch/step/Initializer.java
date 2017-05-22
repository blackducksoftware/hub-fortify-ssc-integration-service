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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
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

    private String startJobTimeStamp;

    private boolean jobStatus = false;

    private final static Logger logger = Logger.getLogger(Initializer.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Started MappingParserTask");
        Arrays.stream(new File(PropertyConstants.getReportDir()).listFiles()).forEach(File::delete);
        logger.info("Found Mapping file:: " + PropertyConstants.getMappingJsonPath());
        final List<BlackDuckFortifyMapper> blackDuckFortifyMappers = MappingParser
                .createMapping(PropertyConstants.getMappingJsonPath());
        logger.info("blackDuckFortifyMappers :" + blackDuckFortifyMappers.toString());

        ExecutorService exec = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>(blackDuckFortifyMappers.size());
        for (BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
            futures.add(exec.submit(new BlackDuckFortifyPushThread(blackDuckFortifyMapper)));
        }
        for (Future<?> f : futures) {
            f.get(); // wait for a processor to complete
        }

        jobStatus = true;
        logger.info("After all threads processing");
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        startJobTimeStamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(LocalDateTime.now());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (jobStatus) {
            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(PropertyConstants.getBatchJobStatusFilePath()), "utf-8"))) {
                writer.write(startJobTimeStamp);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ExitStatus.COMPLETED;
    }
}
