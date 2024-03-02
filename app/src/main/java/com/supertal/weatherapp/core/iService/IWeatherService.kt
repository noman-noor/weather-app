package com.supertal.weatherapp.core.iService

import com.supertal.weatherapp.core.Result
import com.supertal.weatherapp.core.dataModels.AutoComplete
import com.supertal.weatherapp.core.dataModels.CurrentWeather
import com.supertal.weatherapp.core.dataModels.ForecastData
import com.supertal.weatherapp.core.dataModels.ForecastParams
import com.supertal.weatherapp.core.dataModels.WeatherParams
import kotlinx.coroutines.flow.Flow

interface IWeatherService {
    fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>>
    fun autoComplete(query:String):Flow<Result<AutoComplete>>
    fun forecastData(forecastParams: ForecastParams):Flow<Result<ForecastData>>

}