package com.supertal.weatherapp.core.dataModels

data class ForecastUiData(
    val date: String,
    val message: String,
    val imageUrl: String,
    val maxTemp: String,
    val minTemp: String
)
