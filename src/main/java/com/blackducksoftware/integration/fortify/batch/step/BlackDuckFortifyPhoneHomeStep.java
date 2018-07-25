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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody.Builder;

/**
 * @author jfisher
 *
 */
public class BlackDuckFortifyPhoneHomeStep implements Tasklet, StepExecutionListener {
    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPhoneHomeStep.class);

    private final HubServices hubServices;

    private final PropertyConstants propertyConstants;

    /**
     * Constructor
     *
     * @param hubServices
     *            HubServices object which provides access to the Hub
     */
    public BlackDuckFortifyPhoneHomeStep(final HubServices hubServices, final PropertyConstants propertyConstants) {
        this.hubServices = hubServices;
        this.propertyConstants = propertyConstants;
    }

    /**
     * This executes before the step and stores the start time of the step
     */
    @Override
    public void beforeStep(final StepExecution stepExecution) {
    }

    /**
     * This executes after the step and will log the start and end time of the step
     */
    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        /**
         * See http://forum.spring.io/forum/spring-projects/batch/123268-endtime-not-set-on-stepexecution for
         * discussion on why StepExecution.endTime is null when the listener is called.
         */
        logger.info("Phone Home execution step started: " + stepExecution.getStartTime() + " and ended: " + new Date());
        return null;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        logger.info("Started Phone Home step");

        final PhoneHomeService phoneHome = hubServices.createPhoneHomeDataService();
        final Builder phoneHomeReq = phoneHome.createInitialPhoneHomeRequestBodyBuilder("fortify-ssc", propertyConstants.getPluginVersion());
        phoneHomeReq.addToMetaData("Source", "Alliance Integrations");
        phoneHome.phoneHome(phoneHomeReq.build());
        return RepeatStatus.FINISHED;
    }

}
