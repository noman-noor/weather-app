package com.supertal.remotedatasource


import com.google.gson.Gson
import com.islam360.core.common.Result
import com.supertal.core.Constant.ERROR_CODE_NONE
import com.supertal.core.iNetwork.IBaseNetwork
import com.supertal.core.iNetwork.INetworkApi
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


open class BaseNetworkImpl : IBaseNetwork, KoinComponent {

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
        url: String,
        params: HashMap<String, Any>,
        headers: HashMap<String, String>,
        type: Class<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            setProperties(url, params, headers)
            val networkApi = get<INetworkApi>(qualifier = named(apiDomain))
            val httpResponse =
                if (apiRequestType == IBaseNetwork.RequestType.GET) networkApi.callGetRequest(
                    apiUrl, apiParams, apiHttpHeaders
                )
                else networkApi.callPostRequest(apiUrl, apiParams)
            val response = Gson().fromJson(httpResponse.string(), type)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(
                e, if (e is HttpException) e.code() else ERROR_CODE_NONE
            )
        }
    }

    private fun sendApiEvent(response: Response<ResponseBody>?) {

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
    override lateinit var apiRequestType: IBaseNetwork.RequestType
    override var apiDomain: String = DOMAIN_OPEN_WEATHER


}