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
package com.blackducksoftware.integration.fortify.batch.step;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;

/**
 * This class will be the first step of the batch job and it will be used to parse the Mapping.json and based on the
 * number of Hub-Fortify mapping, it will create the threads for parallel processing
 *
 * @author smanikantan
 *
 */
public class Initializer implements Tasklet, StepExecutionListener {

    private String startJobTimeStamp;

    private boolean jobStatus = false;

    private final static Logger logger = Logger.getLogger(Initializer.class);

    private final MappingParser mappingParser;

    private final FortifyFileTokenApi fortifyFileTokenApi;

    private final FortifyUploadApi fortifyUploadApi;

    private final HubServices hubServices;

    private final PropertyConstants propertyConstants;

    public Initializer(final MappingParser mappingParser, final FortifyFileTokenApi fortifyFileTokenApi,
            final FortifyUploadApi fortifyUploadApi, final HubServices hubServices, final PropertyConstants propertyConstants) {
        this.mappingParser = mappingParser;
        this.fortifyFileTokenApi = fortifyFileTokenApi;
        this.fortifyUploadApi = fortifyUploadApi;
        this.hubServices = hubServices;
        this.propertyConstants = propertyConstants;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        logger.info("Started MappingParserTask");
        // Delete the files that are error out in previous run
        Arrays.stream(new File(propertyConstants.getReportDir()).listFiles()).forEach(File::delete);
        logger.debug("Found Mapping file:: " + propertyConstants.getMappingJsonPath());

        // Create the mapping between Hub and Fortify
        final List<BlackDuckFortifyMapperGroup> groupMap = mappingParser.createMapping(propertyConstants.getMappingJsonPath());
        logger.info("blackDuckFortifyMappers :" + groupMap.toString());

        // Create the threads for parallel processing
        final ExecutorService exec = Executors.newFixedThreadPool(propertyConstants.getMaximumThreadSize());
        final List<Future<?>> futures = new ArrayList<>(groupMap.size());
        for (final BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup : groupMap) {
            futures.add(exec.submit(new BlackDuckFortifyPushThread(blackDuckFortifyMapperGroup,
                    hubServices, fortifyFileTokenApi, fortifyUploadApi, propertyConstants)));
        }
        for (final Future<?> f : futures) {
            f.get(); // wait for a processor to complete
        }

        jobStatus = true;
        logger.info("After all threads processing");
        return RepeatStatus.FINISHED;
    }

    /**
     * This method will be executed before this step is started and it will store the start job run time
     */
    @Override
    public void beforeStep(final StepExecution stepExecution) {
        final DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        startJobTimeStamp = formatter.format(new Date());
    }

    /**
     * This method will be executed after this step is completed and it will write the start job run time in
     * batch_job_status.txt
     */
    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        if (jobStatus) {
            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(propertyConstants.getBatchJobStatusFilePath()), "utf-8"))) {
                writer.write(startJobTimeStamp);
            } catch (final UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (final FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (final IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return ExitStatus.COMPLETED;
    }
}
