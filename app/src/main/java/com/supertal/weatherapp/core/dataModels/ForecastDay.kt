package com.supertal.weatherapp.core.dataModels


import com.google.gson.annotations.SerializedName

data class ForecastDay(
    @SerializedName("astro")
    val astro: Astro,
    @SerializedName("date")
    val date: String,
    @SerializedName("date_epoch")
    val dateEpoch: Int,
    @SerializedName("day")
    val day: Day
)