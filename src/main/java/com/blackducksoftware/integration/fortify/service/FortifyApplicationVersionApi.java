/**
 * Copyright (C) 2017 Black Duck Software, Inc.
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

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getFortifyUserName(),
            PropertyConstants.getFortifyPassword());

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyApplicationVersionApiService apiService = retrofit.create(FortifyApplicationVersionApiService.class);

    public static FortifyApplicationResponse getApplicationVersionByName(String fields, String filter) throws IOException {
        Call<FortifyApplicationResponse> apiApplicationResponseCall = apiService.getApplicationVersionByName(fields, filter);
        FortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        return applicationAPIResponse;
    }

    public static int createApplicationVersion(CreateApplicationRequest request) throws IOException {
        Call<CreateFortifyApplicationResponse> apiApplicationResponseCall = apiService.createApplicationVersion(request);
        CreateFortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        return applicationAPIResponse.getData().getId();
    }

    public static int updateApplicationAttributes(int parentId, List<UpdateFortifyApplicationAttributesRequest> request) throws IOException {
        Call<ResponseBody> apiApplicationResponseCall = apiService.updateApplicationAttributes(parentId, request);
        int response = apiApplicationResponseCall.execute().code();
        return response;
    }

    public static int commitApplicationVersion(int id, CommitFortifyApplicationRequest request) throws IOException {
        Call<ResponseBody> apiApplicationResponseCall = apiService.commitApplicationVersion(id, request);
        int response = apiApplicationResponseCall.execute().code();
        return response;
    }
}
