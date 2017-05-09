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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FortifyUploadApiService {
    @Multipart
    @Headers({ "Accept: application/xml, text/xml, */*; q=0.01", "Accept-Encoding: gzip, deflate", "Content-Length: 7092" })
    @POST("upload/resultFileUpload.html")
    Call<ResponseBody> uploadVulnerabilityByProjectVersion(@Query("mat") String fileToken, @Part("entityId") long entityId,
            @Part("engineType") RequestBody engineType, @Part MultipartBody.Part file);
}
