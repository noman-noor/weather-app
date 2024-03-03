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

class IWeatherServiceImpl(private val weatherNetwork: IWeatherNetwork) :
    IWeatherService {
    override fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>> =
        weatherNetwork.provideCurrentWeatherUpdate(params)

    override fun autoComplete(query: String): Flow<Result<AutoComplete>> =
        weatherNetwork.autoComplete(query)

    override fun forecastData(forecastParams: ForecastParams): Flow<Result<ForecastData>> = weatherNetwork.forecastData(forecastParams)
}