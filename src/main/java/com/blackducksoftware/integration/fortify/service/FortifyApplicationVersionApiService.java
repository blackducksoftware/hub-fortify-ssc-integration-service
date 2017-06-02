/**
 * Copyright (C) 2017 Black Duck Software, Inc.
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

import java.util.List;

import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateFortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API Service for FORTIFY REST API
 * 
 * @author hsathe
 *
 */
public interface FortifyApplicationVersionApiService {
    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @GET("api/v1/projectVersions")
    Call<FortifyApplicationResponse> getApplicationVersionByName(@Query("fields") String fields, @Query("q") String filter);

    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @POST("api/v1/projectVersions")
    Call<CreateFortifyApplicationResponse> createApplicationVersion(@Body CreateApplicationRequest request);

    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @PUT("api/v1/projectVersions/{parentId}/attributes")
    Call<ResponseBody> updateApplicationAttributes(@Path("parentId") int parentId, @Body List<UpdateFortifyApplicationAttributesRequest> request);

    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @PUT("api/v1/projectVersions/{id}")
    Call<ResponseBody> commitApplicationVersion(@Path("id") int id, @Body CommitFortifyApplicationRequest request);
}
