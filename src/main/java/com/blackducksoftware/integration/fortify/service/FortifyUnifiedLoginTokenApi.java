/*
Copyright (C) 2020 Synopsys, Inc.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.blackducksoftware.integration.fortify.service;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.UnifiedLoginToken;
import com.blackducksoftware.integration.fortify.model.UnifiedLoginTokenResponse;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class will act as a REST client to access the Fortify Unified Login
 * Token Api
 *
 * @author smanikantan
 *
 */
public final class FortifyUnifiedLoginTokenApi {
    private final static Logger logger = Logger.getLogger(FortifyFileTokenApi.class);

    private final OkHttpClient.Builder okBuilder;

    private final Retrofit retrofit;

    private final FortifyUnifiedLoginTokenApiService apiService;

    public FortifyUnifiedLoginTokenApi(final PropertyConstants propertyConstants) {
        okBuilder = getHeader(propertyConstants);
        retrofit = new Retrofit.Builder().baseUrl(propertyConstants.getFortifyServerUrl())
                .addConverterFactory(GsonConverterFactory.create()).client(okBuilder.build()).build();
        apiService = retrofit.create(FortifyUnifiedLoginTokenApiService.class);
    }

    /**
     * Get the Fortify File token to upload any files
     *
     * @param fileToken
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public UnifiedLoginTokenResponse.Data getUnifiedLoginToken(final UnifiedLoginToken unifiedLoginToken)
            throws IOException, IntegrationException {
        final Call<UnifiedLoginTokenResponse> unifiedLoginTokenResponseCall = apiService
                .getUnifiedLoginToken(unifiedLoginToken);
        final UnifiedLoginTokenResponse unifiedLoginTokenResponse;
        try {
            unifiedLoginTokenResponse = unifiedLoginTokenResponseCall.execute().body();
            FortifyExceptionUtil.verifyFortifyResponseCode(unifiedLoginTokenResponse.getResponseCode(),
                    "Fortify Get Unified Login Token Api");
        } catch (final IOException e) {
            logger.error("Error while retrieving the unified login token", e);
            throw new IOException("Error while retrieving the unified login token", e);
        }
        return unifiedLoginTokenResponse.getData();
    }
    
    /**
     * Delete the Fortify Unified Login token
     *
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public void deleteUnifiedLoginToken(final int id) throws IOException, IntegrationException {
        final Call<ResponseBody> deleteTokenResponseCall = apiService.deleteUnifiedLoginToken(id);
        int responseCode;
        try {
            responseCode = deleteTokenResponseCall.execute().code();
            FortifyExceptionUtil.verifyFortifyResponseCode(responseCode, "Fortify Delete Unified Login Token Api");
        } catch (final IOException e) {
            logger.error("Error while deleting the unified login token", e);
            throw new IOException("Error while deleting the unified login token", e);
        }
    }

    private static Builder getHeader(final PropertyConstants propertyConstants) {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (propertyConstants.getLogLevel().equalsIgnoreCase("INFO")) {
            logging.setLevel(Level.BASIC);
        } else {
            logging.setLevel(Level.BODY);
        }
        final OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.authenticator(new Authenticator() {

            @Override
            public Request authenticate(final Route route, final Response response) throws IOException {
                final String credential = Credentials.basic(propertyConstants.getFortifyUserName(),
                        propertyConstants.getFortifyPassword());
                if (credential.equals(response.request().header("Authorization"))) {
                    try {
                        FortifyExceptionUtil.verifyFortifyResponseCode(response.code(),
                                "Unauthorized access of Fortify Api");
                    } catch (final IntegrationException e) {
                        throw new IOException(e);
                    }
                    return null;
                }
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });

        okBuilder.addInterceptor(logging);
        return okBuilder;
    }

}
