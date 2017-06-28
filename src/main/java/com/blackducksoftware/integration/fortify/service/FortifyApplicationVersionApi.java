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

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getFortifyUserName(),
            PropertyConstants.getFortifyPassword());

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyApplicationVersionApiService apiService = retrofit.create(FortifyApplicationVersionApiService.class);

    public static FortifyApplicationResponse getApplicationVersionByName(String fields, String filter) throws IOException, IntegrationException {
        Call<FortifyApplicationResponse> apiApplicationResponseCall = apiService.getApplicationVersionByName(fields, filter);
        FortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        FortifyExceptionUtil.verifyFortifyCustomException(applicationAPIResponse.getResponseCode(), "Fortify Get Application Version Api");
        return applicationAPIResponse;
    }

    public static int createApplicationVersion(CreateApplicationRequest request) throws IOException {
        Call<CreateFortifyApplicationResponse> apiApplicationResponseCall = apiService.createApplicationVersion(request);
        CreateFortifyApplicationResponse applicationAPIResponse;
        try {
            applicationAPIResponse = apiApplicationResponseCall.execute().body();
        } catch (IOException e) {
            logger.error("Unable to createApplicationVersion ", e);
            throw new IOException("Unable to createApplicationVersion ", e);
        }
        return applicationAPIResponse.getData().getId();
    }

    public static int updateApplicationAttributes(int parentId, List<UpdateFortifyApplicationAttributesRequest> request)
            throws IOException, IntegrationException {
        Call<ResponseBody> apiApplicationResponseCall = apiService.updateApplicationAttributes(parentId, request);
        int response;
        try {
            response = apiApplicationResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyCustomException(response, "Fortify Update Application Version Api");
        } catch (IOException e) {
            logger.error("Unable to updateApplicationVersion ", e);
            throw new IOException("Unable to updateApplicationVersion ", e);
        }
        return response;
    }

    public static int commitApplicationVersion(int id, CommitFortifyApplicationRequest request) throws IOException, IntegrationException {
        Call<ResponseBody> apiApplicationResponseCall = apiService.commitApplicationVersion(id, request);
        int response;
        try {
            response = apiApplicationResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyCustomException(response, "Fortify Commit Application Version Api");
        } catch (IOException e) {
            logger.error("Unable to commitApplicationVersion ", e);
            throw new IOException("Unable to commitApplicationVersion ", e);
        }
        return response;
    }
}
