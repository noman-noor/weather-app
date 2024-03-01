package com.supertal.weatherapp.di

import com.supertal.core.iService.IWeatherService
import com.supertal.service.IWeatherServiceImpl
import org.koin.dsl.module


val serviceModule = module {
    factory<IWeatherService> { IWeatherServiceImpl(get()) }

}

