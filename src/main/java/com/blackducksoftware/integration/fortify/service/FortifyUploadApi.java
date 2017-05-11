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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.fortify.model.JobStatusResponse;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@Component
public class FortifyUploadApi {
    @Autowired
    private Environment env;

    private OkHttpClient.Builder okBuilder;

    private OkHttpClient okHttpClient;

    public void init() {
        okBuilder = getHeader(env.getProperty("fortify.username"), env.getProperty("fortify.password"));
        okHttpClient = okBuilder.build();
    }

    public JobStatusResponse uploadVulnerabilityByProjectVersion(String fileToken, long entityIdVal, File file) throws IOException, IllegalArgumentException {
        if (okBuilder == null)
            init();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("entityId", String.valueOf(entityIdVal));
        builder.addFormDataPart("engineType", "BLACKDUCK");
        builder.addFormDataPart("files[]", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(env.getProperty("fortify.server.url") + "upload/resultFileUpload.html?mat=" + fileToken).post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        Serializer serializer = new Persister();
        JobStatusResponse jobStatusResponse;
        try {
            jobStatusResponse = serializer.read(JobStatusResponse.class, response.body().string());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while generating the upload response");
        }

        return jobStatusResponse;
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

        okBuilder.addInterceptor(logging);
        return okBuilder;
    }
}
