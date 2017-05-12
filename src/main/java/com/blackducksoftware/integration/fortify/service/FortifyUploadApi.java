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

public final class FortifyUploadApi extends FortifyService {

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getProperty("fortify.username"),
            PropertyConstants.getProperty("fortify.password"));;

    private final static OkHttpClient okHttpClient = okBuilder.build();

    private final static String URL = PropertyConstants.getProperty("fortify.server.url") + "upload/resultFileUpload.html?mat=";

    public static JobStatusResponse uploadVulnerabilityByProjectVersion(String fileToken, long entityIdVal, File file) throws Exception, IOException {
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
            throw new Exception("Error while generating the upload response");
        }

        return jobStatusResponse;
    }
}
