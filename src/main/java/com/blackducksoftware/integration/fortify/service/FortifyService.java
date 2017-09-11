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

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.FortifyExceptionUtil;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * This class will be used as a base class to create the header for Fortify Api
 *
 * @author smanikantan
 *
 */
public abstract class FortifyService {
    private final PropertyConstants propertyConstants;

    public FortifyService(PropertyConstants propertyConstants) {
        this.propertyConstants = propertyConstants;
    }

    public PropertyConstants getPropertyConstants() {
        return propertyConstants;
    }

    public static Builder getHeader(String userName, String password) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BASIC);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.authenticator(new Authenticator() {

            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(userName, password);
                if (credential.equals(response.request().header("Authorization"))) {
                    try {
                        FortifyExceptionUtil.verifyFortifyResponseCode(response.code(), "Unauthorized access of Fortify Api");
                    } catch (IntegrationException e) {
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
