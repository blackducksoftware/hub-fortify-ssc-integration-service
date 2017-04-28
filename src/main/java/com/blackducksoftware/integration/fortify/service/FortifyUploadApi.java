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

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class FortifyUploadApi {
    @Autowired
    private Environment env;

    private OkHttpClient.Builder okBuilder;

    private Retrofit retrofit;

    private FortifyUploadApiService apiService;

    public void init() {
        okBuilder = getHeader(env.getProperty("FORTIFY_USERNAME"), env.getProperty("FORTIFY_PASSWORD"));
        retrofit = new Retrofit.Builder().baseUrl(env.getProperty("FORTIFY_SERVER_URL")).addConverterFactory(GsonConverterFactory.create())
                .client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyUploadApiService.class);
    }

    public Call<ResponseBody> uploadVulnerabilityByProjectVersion(String fileToken, long entityId, File file) throws IOException {
        if (okBuilder == null)
            init();
        RequestBody mFile = RequestBody.create(MediaType.parse("application/zip"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("files[]", file.getName(), mFile);
        Call<ResponseBody> uploadVulnerabilityResponse = apiService.uploadVulnerabilityByProjectVersion(fileToken, entityId, fileToUpload);
        return uploadVulnerabilityResponse;
    }

    private Builder getHeader(String userName, String password) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        okBuilder.authenticator(new Authenticator() {

            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(userName, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });

        okBuilder.addInterceptor(logging);
        return okBuilder;
    }
}
