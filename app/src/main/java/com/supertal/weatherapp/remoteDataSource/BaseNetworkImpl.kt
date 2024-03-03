package com.supertal.weatherapp.remoteDataSource


import com.google.gson.Gson
import com.supertal.weatherapp.core.Constant.ERROR_CODE_NONE
import com.supertal.weatherapp.core.Result
import com.supertal.weatherapp.core.iNetwork.IBaseNetwork
import com.supertal.weatherapp.core.iNetwork.INetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import retrofit2.HttpException
import retrofit2.Response


open class BaseNetworkImpl : IBaseNetwork, KoinComponent {

    companion object {
        const val DOMAIN_OPEN_WEATHER = "DOMAIN_OPEN_WEATHER"

    }


    override suspend fun <T> requestFromServer(
        url: String, params: HashMap<String, Any>, headers: HashMap<String, String>, type: Class<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            setProperties(url, params, headers)
            val networkApi = get<INetworkApi>(qualifier = named(apiDomain))
            val httpResponse =
                if (apiRequestType == IBaseNetwork.RequestType.GET) networkApi.callGetRequest(
                    url, params, headers
                )
                else networkApi.callPostRequest(url, params)
            setHttpApiProperties(httpResponse)
            when (httpResponse.code()) {
                200 -> {
                    val response = Gson().fromJson(httpResponse.body()?.string(), type)
                    Result.Success(response)
                }

                else -> {
                    Result.Error(
                        Exception(httpResponse.message()), httpResponse.code()
                    )
                }
            }

        } catch (e: Exception) {
            Result.Error(
                e, if (e is HttpException) e.code() else ERROR_CODE_NONE
            )
        }
    }

    private fun setHttpApiProperties(httpResponse: Response<ResponseBody>?) {
        httpResponse?.apply {
            apiHttpResponse = body()?.toString().orEmpty()
            apiHttpResponseCode = code()
            apiErrorMessage = errorBody()?.string().orEmpty()
        }
    }


    private fun setProperties(
        url: String, params: HashMap<String, Any>, headers: HashMap<String, String>
    ) {
        params["key"] = "7218f129e2d14524a86105818242802"
        apiUrl = url
        apiParams = params
        apiHttpHeaders = headers

    }

    override lateinit var apiHttpHeaders: HashMap<String, String>
    override lateinit var apiHttpResponse: String
    override lateinit var apiMethod: String
    override var apiHttpResponseCode: Int = 0
    override lateinit var apiUrl: String
    override lateinit var apiErrorMessage: String
    override val apiSuccessCode: Int = 1
    override var apiParams: HashMap<String, Any> = hashMapOf()
    override lateinit var apiRequestType: IBaseNetwork.RequestType
    override var apiDomain: String = DOMAIN_OPEN_WEATHER


}