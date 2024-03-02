package com.supertal.weatherapp.remoteDataSource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class WeatherNetworkImpl(
    private val baseNetwork: com.supertal.weatherapp.core.iNetwork.IBaseNetwork,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : com.supertal.weatherapp.core.iNetwork.IWeatherNetwork {
    override fun provideCurrentWeatherUpdate(params: com.supertal.weatherapp.core.dataModels.WeatherParams): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.CurrentWeather>> =
        channelFlow {
            try {
                trySend(com.supertal.weatherapp.core.Result.Loading)
                baseNetwork.apiRequestType = com.supertal.weatherapp.core.iNetwork.IBaseNetwork.RequestType.GET
                val queryParams = baseNetwork.apiParams
                queryParams["q"] = params.query
                queryParams["aqi"] = "yes"
                val response = baseNetwork.requestFromServer(
                    "current.json",
                    queryParams,
                    hashMapOf("Content-Type" to "application/json"),
                    com.supertal.weatherapp.core.dataModels.CurrentWeather::class.java
                )
                trySend(response)
            } catch (ex: Exception) {
                trySend(com.supertal.weatherapp.core.Result.Error(ex))
            }

        }.flowOn(ioDispatcher)

    override fun autoComplete(query: String): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.AutoComplete>> = channelFlow {
        try {
            trySend(com.supertal.weatherapp.core.Result.Loading)
            baseNetwork.apiRequestType = com.supertal.weatherapp.core.iNetwork.IBaseNetwork.RequestType.GET
            val queryParams = baseNetwork.apiParams
            queryParams["q"] = query
            val response = baseNetwork.requestFromServer(
                "search.json",
                queryParams,
                hashMapOf("Content-Type" to "application/json"),
                com.supertal.weatherapp.core.dataModels.AutoComplete::class.java
            )
            trySend(response)
        } catch (ex: Exception) {
            trySend(com.supertal.weatherapp.core.Result.Error(ex))
        }
    }

    override fun forecastData(params: com.supertal.weatherapp.core.dataModels.ForecastParams): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.ForecastData>> = channelFlow {
        try {
            trySend(com.supertal.weatherapp.core.Result.Loading)
            baseNetwork.apiRequestType = com.supertal.weatherapp.core.iNetwork.IBaseNetwork.RequestType.GET
            val queryParams = baseNetwork.apiParams
            queryParams["days"] = params.noOfDays
            queryParams["q"] = params.query
            val response = baseNetwork.requestFromServer(
                "forecast.json",
                queryParams,
                hashMapOf("Content-Type" to "application/json"),
                com.supertal.weatherapp.core.dataModels.ForecastData::class.java
            )
            trySend(response)
        } catch (ex: Exception) {
            trySend(com.supertal.weatherapp.core.Result.Error(ex))
        }
    }
}