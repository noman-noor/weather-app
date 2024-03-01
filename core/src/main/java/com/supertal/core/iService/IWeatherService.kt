package com.supertal.core.iService

import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.Forecast
import com.supertal.core.dataModels.ForecastData
import com.supertal.core.dataModels.ForecastParams
import com.supertal.core.dataModels.WeatherParams
import kotlinx.coroutines.flow.Flow

interface IWeatherService {
    fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>>
    fun autoComplete(query:String):Flow<Result<AutoComplete>>
    fun forecastData(forecastParams: ForecastParams):Flow<Result<ForecastData>>

}