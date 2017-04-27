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

import com.blackducksoftware.integration.fortify.datamodel.FileToken;
import com.blackducksoftware.integration.fortify.datamodel.FileTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FortifyFileTokenApiService {
    @Headers({ "Accept: application/json", "Content-Type:application/json" })
    @POST("api/v1/fileTokens")
    Call<FileTokenResponse> getFileToken(@Body FileToken fileToken);
}
