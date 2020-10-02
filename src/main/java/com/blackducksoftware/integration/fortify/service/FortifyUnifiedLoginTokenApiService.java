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

import com.blackducksoftware.integration.fortify.model.UnifiedLoginToken;
import com.blackducksoftware.integration.fortify.model.UnifiedLoginTokenResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FortifyUnifiedLoginTokenApiService {
    @Headers({ "Accept: application/json", "Content-Type:application/json; charset=utf-8" })
    @POST("api/v1/tokens")
    Call<UnifiedLoginTokenResponse> getUnifiedLoginToken(@Body UnifiedLoginToken unifiedLoginToken);
    
    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @DELETE("api/v1/tokens/{id}")
    Call<ResponseBody> deleteUnifiedLoginToken(@Path("id") int id);
}
