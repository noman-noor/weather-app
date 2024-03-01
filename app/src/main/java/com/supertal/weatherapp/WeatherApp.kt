package com.supertal.weatherapp

import android.app.Application
import com.supertal.weatherapp.di.networkModule
import com.supertal.weatherapp.di.serviceModule
import com.supertal.weatherapp.di.viewModelModule
import com.supertal.weatherapp.utils.ReleaseTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

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
        plantTimber()
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        "%s:%s",
                        element.lineNumber,
                        super.createStackElementTag(element)
                    )
                }
            })
        else Timber.plant(ReleaseTree())
    }
}