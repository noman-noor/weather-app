package com.supertal.weatherapp.di

import com.supertal.weatherapp.core.iService.IWeatherService
import com.supertal.weatherapp.service.IWeatherServiceImpl
import org.koin.dsl.module


val serviceModule = module {
    factory<IWeatherService> { IWeatherServiceImpl(get()) }

}

