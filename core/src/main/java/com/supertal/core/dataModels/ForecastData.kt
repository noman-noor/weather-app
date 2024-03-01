package com.supertal.core.dataModels


import com.google.gson.annotations.SerializedName

data class ForecastData(
    @SerializedName("current")
    val current: Current,
    @SerializedName("forecast")
    val forecast: Forecast,
    @SerializedName("location")
    val location: Location
)