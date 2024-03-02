package com.supertal.weatherapp.remoteDataSource


import com.google.gson.Gson
import com.supertal.weatherapp.core.Constant.ERROR_CODE_NONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.ParameterizedType
import java.net.URI


open class BaseNetworkImpl : com.supertal.weatherapp.core.iNetwork.IBaseNetwork, KoinComponent {

    companion object {
        const val DOMAIN_OPEN_WEATHER = "DOMAIN_OPEN_WEATHER"

    }

    override suspend fun <T> request(
        url: String,
        params: LinkedHashMap<String, Any>,
        headers: HashMap<String, String>,
        type: T,
        parameterizedType: ParameterizedType
    ): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateUrlFromParams(map: HashMap<String, String>): String {
        var url: String = ""
        map.forEach { (_, value) ->
            url = "$url$value/"
        }
        return url
    }


    override suspend fun <T> requestFromServer(
        url: String, params: HashMap<String, Any>, headers: HashMap<String, String>, type: Class<T>
    ): com.supertal.weatherapp.core.Result<T> = withContext(Dispatchers.IO) {
        try {
            setProperties(url, params, headers)
            val networkApi = get<com.supertal.weatherapp.core.iNetwork.INetworkApi>(qualifier = named(apiDomain))
            val httpResponse =
                if (apiRequestType == com.supertal.weatherapp.core.iNetwork.IBaseNetwork.RequestType.GET) networkApi.callGetRequest(
                    apiUrl, apiParams, apiHttpHeaders
                )
                else networkApi.callPostRequest(apiUrl, apiParams)
            setHttpApiProperties(httpResponse)
            when (httpResponse.code()) {
                200 -> {
                    val response = Gson().fromJson(httpResponse.body()?.string(), type)
                    com.supertal.weatherapp.core.Result.Success(response)
                }
                else -> {
                    com.supertal.weatherapp.core.Result.Error(Exception(httpResponse.message()),
                        httpResponse.code())
                }
            }

        } catch (e: Exception) {
            com.supertal.weatherapp.core.Result.Error(
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
        apiUrl = url
        apiParams = params
        apiHttpHeaders = headers

    }

    override lateinit var apiHttpHeaders: HashMap<String, String>
    override lateinit var apiHttpResponse: String
    override lateinit var apiMethod: String
    override var apiHttpResponseCode: Int = 0
    override lateinit var apiUrl: String
    override val apiEndPoint: String get() = URI(this.apiUrl).path
    override lateinit var apiErrorMessage: String
    override val apiSuccessCode: Int = 1
    override var apiParams: HashMap<String, Any> =
        hashMapOf("key" to "7218f129e2d14524a86105818242802")
    override lateinit var apiRequestType: com.supertal.weatherapp.core.iNetwork.IBaseNetwork.RequestType
    override var apiDomain: String = DOMAIN_OPEN_WEATHER


}