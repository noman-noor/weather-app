package com.supertal.remotedatasource

import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.WeatherParams
import com.supertal.core.iNetwork.IBaseNetwork
import com.supertal.core.iNetwork.IWeatherNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherNetworkImpl(
    private val baseNetwork: IBaseNetwork,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IWeatherNetwork {
    override fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>> =
        flow {
            try {
                baseNetwork.apiRequestType = IBaseNetwork.RequestType.GET
                val queryParams = baseNetwork.apiParams
                queryParams["q"] = params.query
                queryParams["aqi"] = "yes"
                val response = baseNetwork.requestFromServer(
                    "current.json",
                    queryParams,
                    hashMapOf("Content-Type" to "application/json"),
                    CurrentWeather::class.java
                )
                emit(response)
            } catch (ex: Exception) {
                emit(Result.Error(ex))
            }

        }.flowOn(ioDispatcher)

    override fun autoComplete(query: String): Flow<Result<AutoComplete>> = flow {
        try {
            emit(Result.Loading)
            baseNetwork.apiRequestType = IBaseNetwork.RequestType.GET
            val queryParams = baseNetwork.apiParams
            queryParams["q"] = query
            val response = baseNetwork.requestFromServer(
                "search.json",
                queryParams,
                hashMapOf("Content-Type" to "application/json"),
                AutoComplete::class.java
            )
            emit(response)
        } catch (ex: Exception) {
            emit(Result.Error(ex))
        }
    }
}