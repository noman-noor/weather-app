package com.supertal.core.iService

import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.WeatherParams
import kotlinx.coroutines.flow.Flow

interface IWeatherService {
    fun provideCurrentWeatherUpdate(params: WeatherParams): Flow<Result<CurrentWeather>>
    fun autoComplete(query:String):Flow<Result<AutoComplete>>
}