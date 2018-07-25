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
package com.blackducksoftware.integration.fortify.batch.util;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;

public final class FortifyExceptionUtil {
    private final static Logger logger = Logger.getLogger(FortifyExceptionUtil.class);

    public static void verifyFortifyResponseCode(final int responseCode, final String apiName) throws IntegrationException {
        switch (responseCode) {
        case 200:
            logger.info("Response code::" + responseCode + " ~ " + apiName + " executed successfuly");
            break;
        case 201:
            logger.info("Response code::" + responseCode + " ~ " + apiName + " executed successfuly");
            break;
        case 400:
            logger.error("Response code::" + responseCode + " ~ " + "Unauthorized access of " + apiName);
            throw new IntegrationException("Response code::" + responseCode + " ~ " + "Unauthorized access of " + apiName);
        case 401:
            logger.error("Response code::" + responseCode + " ~ " + apiName);
            throw new IntegrationException("Response code::" + responseCode + " ~ " + apiName);
        case 403:
            logger.error("Response code::" + responseCode + " ~ " + "Forbidden request of " + apiName);
            throw new IntegrationException("Response code::" + responseCode + " ~ " + "Forbidden request of " + apiName);
        case 404:
            logger.error("Response code::" + responseCode + " ~ " + apiName + " not found");
            throw new IntegrationException("Response code::" + responseCode + " ~ " + apiName + " not found");
        default:
            logger.error("Response code::" + responseCode + " ~ " + "Unknown error to get the response of " + apiName);
            throw new IntegrationException("Response code::" + responseCode + " ~ " + "Unknown error to get the response of " + apiName);
        }
    }

    public static void throwFortifyCustomException(final int responseCode, final String apiName, final String errorMessage) throws IntegrationException {
        logger.error("Response code::" + responseCode + ", Api::" + apiName + ", Error message::" + errorMessage);
        throw new IntegrationException("Response code::" + responseCode + ", Api::" + apiName + ", Error message::" + errorMessage);
    }
}
