package com.supertal.weatherapp

import android.app.Application
import com.supertal.weatherapp.di.networkModule
import com.supertal.weatherapp.di.serviceModule
import com.supertal.weatherapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApp)
            androidLogger()
            modules(
                listOf(viewModelModule, serviceModule, networkModule)
            )
            loadKoinModules(module(override = true) { single { this@WeatherApp } })
        }
    }
}