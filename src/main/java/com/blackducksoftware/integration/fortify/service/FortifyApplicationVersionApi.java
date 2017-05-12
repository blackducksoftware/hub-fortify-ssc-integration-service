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
package com.blackducksoftware.integration.fortify.service;

import java.io.IOException;

import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class FortifyApplicationVersionApi extends FortifyService {

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getProperty("fortify.username"),
            PropertyConstants.getProperty("fortify.password"));;

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getProperty("fortify.server.url"))
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyApplicationVersionApiService apiService = retrofit.create(FortifyApplicationVersionApiService.class);

    public static FortifyApplicationResponse getApplicationByName(String fields, String filter) throws IOException {
        Call<FortifyApplicationResponse> apiApplicationResponseCall = apiService.getApplicationByName(fields, filter);
        FortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        return applicationAPIResponse;
    }
}
