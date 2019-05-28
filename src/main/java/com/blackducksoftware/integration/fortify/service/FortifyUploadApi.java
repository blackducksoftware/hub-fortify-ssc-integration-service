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
package com.blackducksoftware.integration.fortify.service;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class will act as a REST client to access the Fortify Upload Api
 *
 * @author smanikantan
 *
 */
public final class FortifyUploadApi extends FortifyService {
    private final static Logger logger = Logger.getLogger(FortifyUploadApi.class);

    private final OkHttpClient.Builder okBuilder;

    private final OkHttpClient okHttpClient;

    private final String URL;

    public FortifyUploadApi(final PropertyConstants propertyConstants) {
        super(propertyConstants);
        okBuilder = getHeader(propertyConstants);
        okHttpClient = okBuilder.build();
        URL = propertyConstants.getFortifyServerUrl() + "upload/resultFileUpload.html?mat=";
    }

    /**
     * Upload the vulnerabilities to Fortify
     *
     * @param fileToken
     * @param entityIdVal
     * @param file
     * @return
     * @throws Exception
     */
    public boolean uploadVulnerabilityByProjectVersion(final String fileToken, final long entityIdVal, final File file) throws IOException {
        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("entityId", String.valueOf(entityIdVal));
        builder.addFormDataPart("engineType", "BLACKDUCK");
        builder.addFormDataPart("files[]", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file));

        final RequestBody requestBody = builder.build();

        final Request request = new Request.Builder().url(URL + fileToken).post(requestBody).build();
        Response response;
        JobStatusResponse jobStatusResponse = null;
        try {
            response = okHttpClient.newCall(request).execute();
            final Serializer serializer = new Persister();
            try {
                jobStatusResponse = serializer.read(JobStatusResponse.class, response.body().string());
                if (jobStatusResponse != null && jobStatusResponse.getCode() == -10001
                        && "Background submission succeeded.".equalsIgnoreCase(jobStatusResponse.getMessage())) {
                    return true;
                } else {
                    FortifyExceptionUtil.throwFortifyCustomException(jobStatusResponse.getCode(), "Fortify Upload Api",
                            jobStatusResponse.getMessage());
                }
            } catch (final Exception e) {
                logger.error("Error while reading the fortify upload response", e);
            }
        } catch (final IOException e) {
            logger.error("Error while uploading the vulnerability to Fortify", e);
            throw new IOException("Error while uploading the vulnerability to Fortify", e);
        }

        return false;
    }
}
