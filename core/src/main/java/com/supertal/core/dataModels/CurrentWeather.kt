package com.supertal.core.dataModels


import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("current")
    val current: Current,
    @SerializedName("location")
    val location: Location
)