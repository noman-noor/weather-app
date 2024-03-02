package com.supertal.weatherapp.service

import com.supertal.weatherapp.core.Result
import com.supertal.weatherapp.core.dataModels.AutoComplete
import com.supertal.weatherapp.core.dataModels.CurrentWeather
import com.supertal.weatherapp.core.dataModels.ForecastData
import com.supertal.weatherapp.core.dataModels.ForecastParams
import com.supertal.weatherapp.core.dataModels.WeatherParams
import com.supertal.weatherapp.core.iNetwork.IWeatherNetwork
import com.supertal.weatherapp.core.iService.IWeatherService
import kotlinx.coroutines.flow.Flow

class IWeatherServiceImpl(private val weatherNetwork: com.supertal.weatherapp.core.iNetwork.IWeatherNetwork) :
    com.supertal.weatherapp.core.iService.IWeatherService {
    override fun provideCurrentWeatherUpdate(params: com.supertal.weatherapp.core.dataModels.WeatherParams): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.CurrentWeather>> =
        weatherNetwork.provideCurrentWeatherUpdate(params)

    override fun autoComplete(query: String): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.AutoComplete>> =
        weatherNetwork.autoComplete(query)

    override fun forecastData(forecastParams: com.supertal.weatherapp.core.dataModels.ForecastParams): Flow<com.supertal.weatherapp.core.Result<com.supertal.weatherapp.core.dataModels.ForecastData>> = weatherNetwork.forecastData(forecastParams)
}