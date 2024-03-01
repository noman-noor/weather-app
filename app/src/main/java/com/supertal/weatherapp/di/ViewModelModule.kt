package com.supertal.weatherapp.di

import com.supertal.weatherapp.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { HomeViewModel(get()) }

}

