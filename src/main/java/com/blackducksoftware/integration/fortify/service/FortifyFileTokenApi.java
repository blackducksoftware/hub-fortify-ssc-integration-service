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

import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class FortifyFileTokenApi {
    @Autowired
    private Environment env;

    private OkHttpClient.Builder okBuilder;

    private Retrofit retrofit;

    private FortifyFileTokenApiService apiService;

    private void init() {
        okBuilder = getHeader(env.getProperty("FORTIFY_USERNAME"), env.getProperty("FORTIFY_PASSWORD"));
        retrofit = new Retrofit.Builder().baseUrl(env.getProperty("FORTIFY_SERVER_URL")).addConverterFactory(GsonConverterFactory.create())
                .client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyFileTokenApiService.class);
    }

    public FileTokenResponse getFileToken(FileToken fileToken) throws IOException {
        if (okBuilder == null)
            init();
        Call<FileTokenResponse> fileTokenResponseCall = apiService.getFileToken(fileToken);
        FileTokenResponse fileTokenResponse = fileTokenResponseCall.execute().body();
        return fileTokenResponse;
    }

    public int deleteFileToken() throws IOException {
        if (okBuilder == null)
            init();
        Call<ResponseBody> deleteTokenResponseCall = apiService.deleteFileToken();
        int responseCode = deleteTokenResponseCall.execute().code();
        return responseCode;
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
