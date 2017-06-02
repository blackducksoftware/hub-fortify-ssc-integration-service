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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

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

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getFortifyUserName(),
            PropertyConstants.getFortifyPassword());

    private final static OkHttpClient okHttpClient = okBuilder.build();

    private final static String URL = PropertyConstants.getFortifyServerUrl() + "upload/resultFileUpload.html?mat=";

    /**
     * Upload the vulnerabilities to Fortify
     *
     * @param fileToken
     * @param entityIdVal
     * @param file
     * @return
     * @throws Exception
     * @throws IOException
     */
    public static JobStatusResponse uploadVulnerabilityByProjectVersion(String fileToken, long entityIdVal, File file) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("entityId", String.valueOf(entityIdVal));
        builder.addFormDataPart("engineType", "BLACKDUCK");
        builder.addFormDataPart("files[]", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(URL + fileToken).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        Serializer serializer = new Persister();
        JobStatusResponse jobStatusResponse;
        try {
            jobStatusResponse = serializer.read(JobStatusResponse.class, response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while reading the upload response", e);
        }

        return jobStatusResponse;
    }
}
