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

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class will act as a REST client to access the Fortify File Token Api
 *
 * @author smanikantan
 *
 */
public final class FortifyFileTokenApi extends FortifyService {

    private final static Logger logger = Logger.getLogger(FortifyFileTokenApi.class);

    private final static OkHttpClient.Builder okBuilder = getHeader(PropertyConstants.getFortifyUserName(),
            PropertyConstants.getFortifyPassword());

    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(PropertyConstants.getFortifyServerUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();

    private final static FortifyFileTokenApiService apiService = retrofit.create(FortifyFileTokenApiService.class);

    /**
     * Get the Fortify File token to upload any files
     *
     * @param fileToken
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public static String getFileToken(FileToken fileToken) throws IOException, IntegrationException {
        Call<FileTokenResponse> fileTokenResponseCall = apiService.getFileToken(fileToken);
        FileTokenResponse fileTokenResponse;
        try {
            fileTokenResponse = fileTokenResponseCall.execute().body();
            FortifyExceptionUtil.verifyFortifyCustomException(fileTokenResponse.getResponseCode(), "Fortify Upload Get File Token Api");
        } catch (IOException e) {
            logger.error("Error while retrieving the file token for upload", e);
            throw new IOException("Error while retrieving the file token for upload", e);
        }
        return fileTokenResponse.getData().getToken();
    }

    /**
     * Delete the Fortify file token that is created for upload
     *
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public static int deleteFileToken() throws IOException, IntegrationException {
        Call<ResponseBody> deleteTokenResponseCall = apiService.deleteFileToken();
        int responseCode;
        try {
            responseCode = deleteTokenResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyCustomException(responseCode, "Fortify Upload Delete File Token Api");
        } catch (IOException e) {
            logger.error("Error while deleting the file token for upload", e);
            throw new IOException("Error while deleting the file token for upload", e);
        }
        return responseCode;
    }
}
