package com.supertal.weatherapp.core.iNetwork

import com.supertal.weatherapp.core.Result
import java.lang.reflect.ParameterizedType

interface IBaseNetwork {


    var apiUrl: String
    var apiErrorMessage: String
    val apiSuccessCode: Int
    var apiParams: HashMap<String, Any>
    var apiHttpHeaders: HashMap<String, String>
    var apiHttpResponse: String
    var apiMethod: String
    var apiHttpResponseCode: Int
    var apiRequestType: RequestType
    var apiDomain:String

    enum class RequestType {
        GET, POST
    }


    suspend fun <T> requestFromServer(
        url: String,
        params: HashMap<String, Any>,
        headers: HashMap<String, String>,
        type: Class<T>,
    ): Result<T>


}