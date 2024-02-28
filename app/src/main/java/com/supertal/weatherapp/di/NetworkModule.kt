package com.supertal.weatherapp.di

import com.islam360.network.BaseNetworkImpl
import com.islam360.network.BaseNetworkImpl.Companion.DOMAIN_OPEN_WEATHER
import com.supertal.core.EnvironmentConstant.BASE_URL
import com.supertal.core.iNetwork.IBaseNetwork
import com.supertal.core.iNetwork.INetworkApi
import com.supertal.core.iNetwork.IWeatherNetwork
import com.supertal.remotedatasource.WeatherNetworkImpl
import com.supertal.weatherapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val networkModule = module {
    factory<IWeatherNetwork> { WeatherNetworkImpl(get()) }


    factory<IBaseNetwork> { BaseNetworkImpl() }
    single { provideHttpLoggingInterceptor() }
    single { provideOkHttpClient(get()) }
    single(qualifier = named(DOMAIN_OPEN_WEATHER)) { provideWeatherApi(get()) }
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {

    val okHttpClient = OkHttpClient().newBuilder()
    if (BuildConfig.DEBUG) okHttpClient.addInterceptor(interceptor)

    return okHttpClient.connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS).cache(null).build()
}


fun provideWeatherApi(okHttpClient: OkHttpClient): INetworkApi =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient).build().create(INetworkApi::class.java)