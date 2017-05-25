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
 *
 *
 * API Service for FORTIFY REST API
 *
 * @author: hsathe
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
