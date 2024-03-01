package com.supertal.service

import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.Forecast
import com.supertal.core.dataModels.ForecastData
import com.supertal.core.dataModels.ForecastParams
import com.supertal.core.dataModels.WeatherParams
import com.supertal.core.iNetwork.IWeatherNetwork
import com.supertal.core.iService.IWeatherService
import kotlinx.coroutines.flow.Flow

class IWeatherServiceImpl(private val weatherNetwork: IWeatherNetwork) : IWeatherService {
    override fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>> =
        weatherNetwork.provideCurrentWeatherUpdate(params)

    override fun autoComplete(query: String): Flow<Result<AutoComplete>> =
        weatherNetwork.autoComplete(query)

    override fun forecastData(forecastParams: ForecastParams): Flow<Result<ForecastData>> = weatherNetwork.forecastData(forecastParams)
}