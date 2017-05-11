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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class FortifyApplicationVersionApi {
    @Autowired
    private Environment env;

    private OkHttpClient.Builder okBuilder;

    private Retrofit retrofit;

    private FortifyApplicationVersionApiService apiService;

    private void init() {
        okBuilder = getHeader(env.getProperty("fortify.username"), env.getProperty("fortify.password"));
        retrofit = new Retrofit.Builder().baseUrl(env.getProperty("fortify.server.url")).addConverterFactory(GsonConverterFactory.create())
                .client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyApplicationVersionApiService.class);
    }

    public FortifyApplicationResponse getApplicationByName(String fields, String filter) throws IOException {
        if (okBuilder == null) {
            init();
        }
        Call<FortifyApplicationResponse> apiApplicationResponseCall = apiService.getApplicationByName(fields, filter);
        FortifyApplicationResponse applicationAPIResponse = apiApplicationResponseCall.execute().body();
        return applicationAPIResponse;
    }

    private Builder getHeader(String userName, String password) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BASIC);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        okBuilder.authenticator(new Authenticator() {

            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(userName, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });

        okBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request;
                try {
                    request = chain.request().newBuilder()
                            .addHeader("Cache-Control", "no-cache").build();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return chain.proceed(request);
            }
        });
        okBuilder.addInterceptor(logging);
        return okBuilder;
    }
}
