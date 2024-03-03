package com.supertal.weatherapp.remoteDataSource

import com.supertal.weatherapp.core.Result
import com.supertal.weatherapp.core.dataModels.AutoComplete
import com.supertal.weatherapp.core.dataModels.CurrentWeather
import com.supertal.weatherapp.core.dataModels.ForecastData
import com.supertal.weatherapp.core.dataModels.WeatherParams
import com.supertal.weatherapp.core.iNetwork.IBaseNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class WeatherNetworkImpl(
    private val baseNetwork: IBaseNetwork,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : com.supertal.weatherapp.core.iNetwork.IWeatherNetwork {
    override fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>> =
        channelFlow {
            try {
                trySend(Result.Loading)
                baseNetwork.apiRequestType = IBaseNetwork.RequestType.GET
                val queryParams = hashMapOf<String, Any>("q" to params.query, "aqi" to "yes")
                val response = baseNetwork.requestFromServer(
                    "current.json",
                    queryParams,
                    hashMapOf("Content-Type" to "application/json"),
                    CurrentWeather::class.java
                )
                trySend(response)
            } catch (ex: Exception) {
                trySend(Result.Error(ex))
            }

        }.flowOn(ioDispatcher)

    override fun autoComplete(query: String): Flow<Result<AutoComplete>> = channelFlow {
        try {
            trySend(Result.Loading)
            baseNetwork.apiRequestType = IBaseNetwork.RequestType.GET
            val queryParams = hashMapOf<String, Any>("q" to query)
            val response = baseNetwork.requestFromServer(
                "search.json",
                queryParams,
                hashMapOf("Content-Type" to "application/json"),
                AutoComplete::class.java
            )
            trySend(response)
        } catch (ex: Exception) {
            trySend(Result.Error(ex))
        }
    }

    override fun forecastData(params: com.supertal.weatherapp.core.dataModels.ForecastParams): Flow<Result<ForecastData>> =
        channelFlow {
            try {
                trySend(Result.Loading)
                baseNetwork.apiRequestType = IBaseNetwork.RequestType.GET
                val queryParams = hashMapOf<String, Any>("q" to params.query, "days" to params.noOfDays)
                val response = baseNetwork.requestFromServer(
                    "forecast.json",
                    queryParams,
                    hashMapOf("Content-Type" to "application/json"),
                    ForecastData::class.java
                )
                trySend(response)
            } catch (ex: Exception) {
                trySend(Result.Error(ex))
            }
        }
}