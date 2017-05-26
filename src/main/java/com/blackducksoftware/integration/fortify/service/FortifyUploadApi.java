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
