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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBodyBuilder;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

/**
 * @author jfisher
 *
 */
public class BlackDuckFortifyPhoneHomeStep implements Tasklet, StepExecutionListener {
    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPhoneHomeStep.class);

    private String startJobTimeStamp;

    private final HubServices hubServices;

    private final PropertyConstants propertyConstants;

    /**
     * Constructor
     *
     * @param hubServices
     *            HubServices object which provides access to the Hub
     */
    public BlackDuckFortifyPhoneHomeStep(HubServices hubServices, PropertyConstants propertyConstants) {
        this.hubServices = hubServices;
        this.propertyConstants = propertyConstants;
    }

    /**
     * This executes before the step and stores the start time of the step
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        startJobTimeStamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(LocalDateTime.now());
    }

    /**
     * This executes after the step and will log the start and end time of the step
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String stopJobTimeStamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(LocalDateTime.now());
        logger.info("Phone Home execution step started: " + startJobTimeStamp + " and ended: " + stopJobTimeStamp);
        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Started Phone Home step");

        PhoneHomeDataService phoneHome = hubServices.getPhoneHomeDataService();
        PhoneHomeRequestBodyBuilder phoneHomeReq = phoneHome.createInitialPhoneHomeRequestBodyBuilder();
        phoneHomeReq.setSource(PhoneHomeSource.ALLIANCES);
        phoneHomeReq.setThirdPartyName(ThirdPartyName.FORTIFY_SSC);
        phoneHomeReq.setThirdPartyVersion("N/A");
        phoneHomeReq.setPluginVersion(propertyConstants.getPluginVersion());

        phoneHome.phoneHome(phoneHomeReq.build());

        return RepeatStatus.FINISHED;
    }

}
