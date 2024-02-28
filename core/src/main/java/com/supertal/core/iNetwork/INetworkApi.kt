package com.supertal.core.iNetwork

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface INetworkApi {

    @GET
    suspend fun callGetRequest(
        @Url url: String,
        @QueryMap params: Map<String, @JvmSuppressWildcards Any>?,
        @HeaderMap headers: HashMap<String, String>
    ): ResponseBody


    @POST
    suspend fun callPostRequest(
        @Url url: String, @Body params: Map<String, @JvmSuppressWildcards Any>
    ): ResponseBody

}