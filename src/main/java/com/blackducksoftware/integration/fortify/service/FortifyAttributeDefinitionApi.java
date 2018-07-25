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

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FortifyAttributeDefinitionResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class FortifyAttributeDefinitionApi extends FortifyService {
    private final static String FILTER_REQUIRE_ATTRIBUTE = "required:true";

    private final static String FIELDS_ATTRIBUTE = "id,name,category,type,options,required";

    private final OkHttpClient.Builder okBuilder;

    private final Retrofit retrofit;

    private final FortifyAttributeDefinitionApiService apiService;

    public FortifyAttributeDefinitionApi(final PropertyConstants propertyConstants) {
        super(propertyConstants);
        okBuilder = getHeader(propertyConstants.getFortifyUserName(),
                propertyConstants.getFortifyPassword());
        retrofit = new Retrofit.Builder().baseUrl(propertyConstants.getFortifyServerUrl())
                .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyAttributeDefinitionApiService.class);
    }

    public FortifyAttributeDefinitionResponse getAttributeDefinitions() throws IOException, IntegrationException {
        final Call<FortifyAttributeDefinitionResponse> apiApplicationResponseCall = apiService.getAttributeDefinitions(FIELDS_ATTRIBUTE,
                FILTER_REQUIRE_ATTRIBUTE);
        final Response<FortifyAttributeDefinitionResponse> applicationAPIResponse = apiApplicationResponseCall.execute();
        FortifyExceptionUtil.verifyFortifyResponseCode(applicationAPIResponse.code(), "Fortify Get Application Attributes Api");
        return applicationAPIResponse.body();
    }
}
