package com.supertal.core.dataModels

data class CurrentWeatherUiData(
    val location: String,
    val time: String,
    private val tempC: Double,
    private val tempF: Double,
    private val feelsLikeTempC: Double,
    private val feelsLikeTempF: Double,
    val weatherImageUrl: String,
    val welcomeMessage: String,
    var unit: String = "c",
    val windSpeed: String,
    val cloudCover: String
) {

    val formattedTemp: String
        get() {
            return when (unit) {
                "c" -> {
                    formattedTempInC
                }

                else -> {
                    formattedTempInF

                }
            }
        }

    val formattedTempFeelsLike: String
        get() {
            return when (unit) {
                "c" -> {
                    formattedFeelsLikeTempInC
                }

                else -> {
                    formattedFeelsLikeTempInF

                }
            }
        }


    private val formattedTempInC: String
        get() {
            return tempC.toInt().toString() + 0x00B0.toChar() + "C"
        }

    private val formattedTempInF: String
        get() {
            return tempF.toInt().toString() + 0x00B0.toChar() + "F"
        }

    private val formattedFeelsLikeTempInC: String
        get() {
            return feelsLikeTempC.toInt().toString() + 0x00B0.toChar() + "C"
        }

    private val formattedFeelsLikeTempInF: String
        get() {
            return feelsLikeTempF.toInt().toString() + 0x00B0.toChar() + "F"
        }
}
