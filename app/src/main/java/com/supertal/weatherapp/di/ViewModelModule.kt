package com.supertal.weatherapp.di

import com.islam360.network.BaseNetworkImpl.Companion.DOMAIN_OPEN_WEATHER
import com.supertal.core.EnvironmentConstant.BASE_URL
import com.supertal.core.iNetwork.INetworkApi
import com.supertal.core.iNetwork.IWeatherNetwork
import com.supertal.core.iService.IWeatherService
import com.supertal.remotedatasource.WeatherNetworkImpl
import com.supertal.service.IWeatherServiceImpl
import com.supertal.weatherapp.BuildConfig
import com.supertal.weatherapp.home.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { HomeViewModel(get()) }

}

