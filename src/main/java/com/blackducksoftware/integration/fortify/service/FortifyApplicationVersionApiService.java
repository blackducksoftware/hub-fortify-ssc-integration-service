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
 * @author: hsathe
 */
package com.blackducksoftware.integration.fortify.service;

import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FortifyApplicationVersionApiService {
    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @GET("api/v1/projectVersions")
    Call<FortifyApplicationResponse> getApplicationByName(@Query("fields") String fields, @Query("q") String filter);
}
