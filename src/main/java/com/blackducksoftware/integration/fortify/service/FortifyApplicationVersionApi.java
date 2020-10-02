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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.MappingParser;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateFortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API service for Fortify REST API
 *
 * @author hsathe
 *
 */
public final class FortifyApplicationVersionApi extends FortifyService {

    private final static Logger logger = Logger.getLogger(MappingParser.class);

    private final OkHttpClient.Builder okBuilder;

    private final Retrofit retrofit;

    private final FortifyApplicationVersionApiService apiService;

    public FortifyApplicationVersionApi(final PropertyConstants propertyConstants, final String token) {
        super(propertyConstants);
        okBuilder = getHeader(propertyConstants, token);
        retrofit = new Retrofit.Builder().baseUrl(propertyConstants.getFortifyServerUrl())
                .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyApplicationVersionApiService.class);
    }

    public FortifyApplicationResponse getApplicationVersionByName(final String fields, final String filter) throws IOException, IntegrationException {
        final Call<FortifyApplicationResponse> apiApplicationResponseCall = apiService.getApplicationVersionByName(fields, filter);
        final FortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        FortifyExceptionUtil.verifyFortifyResponseCode(applicationAPIResponse.getResponseCode(), "Fortify Get Application Version Api");
        return applicationAPIResponse;
    }

    public int createApplicationVersion(final CreateApplicationRequest request) throws IOException {
        final Call<CreateFortifyApplicationResponse> apiApplicationResponseCall = apiService.createApplicationVersion(request);
        CreateFortifyApplicationResponse applicationAPIResponse;
        try {
            applicationAPIResponse = apiApplicationResponseCall.execute().body();
        } catch (final IOException e) {
            logger.error("Unable to createApplicationVersion ", e);
            throw new IOException("Unable to createApplicationVersion ", e);
        }
        return applicationAPIResponse.getData().getId();
    }

    public void updateApplicationAttributes(final int parentId, final List<UpdateFortifyApplicationAttributesRequest> request)
            throws IOException, IntegrationException {
        final Call<ResponseBody> apiApplicationResponseCall = apiService.updateApplicationAttributes(parentId, request);
        int response;
        try {
            response = apiApplicationResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyResponseCode(response, "Fortify Update Application Version Api");
        } catch (final IOException e) {
            logger.error("Unable to updateApplicationVersion ", e);
            throw new IOException("Unable to updateApplicationVersion ", e);
        }
    }

    public void commitApplicationVersion(final int id, final CommitFortifyApplicationRequest request) throws IOException, IntegrationException {
        final Call<ResponseBody> apiApplicationResponseCall = apiService.commitApplicationVersion(id, request);
        int response;
        try {
            response = apiApplicationResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyResponseCode(response, "Fortify Commit Application Version Api");
        } catch (final IOException e) {
            logger.error("Unable to commitApplicationVersion ", e);
            throw new IOException("Unable to commitApplicationVersion ", e);
        }
    }

    public void deleteApplicationVersion(final int id) throws IOException, IntegrationException {
        final Call<ResponseBody> apiApplicationResponseCall = apiService.deleteApplicationVersion(id);
        int response;
        try {
            response = apiApplicationResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyResponseCode(response, "Fortify Delete Application Version Api");
        } catch (final IOException e) {
            logger.error("Unable to deleteApplicationVersion ", e);
            throw new IOException("Unable to deleteApplicationVersion ", e);
        }
    }
}
