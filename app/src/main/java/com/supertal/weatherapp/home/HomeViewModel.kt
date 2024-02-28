package com.supertal.weatherapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.WeatherParams
import com.supertal.core.iService.IWeatherService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val weatherService: IWeatherService) : ViewModel() {

    private val _autoComplete = MutableStateFlow(AutoComplete())
    val autoComplete: StateFlow<AutoComplete> = _autoComplete

    private val _uiEvent = Channel<UiEvents>(Channel.CONFLATED)
    private val uiEvents = _uiEvent.receiveAsFlow()

    fun loadCurrentWeather() {
        viewModelScope.launch {
            weatherService.provideCurrentWeatherUpdate(WeatherParams("karachi"))
                .collect { resonse ->
                    when (resonse) {
                        is Result.Error -> {
                            resonse.exception.printStackTrace()
                        }

                        Result.Loading -> {
                            Timber.tag("Loading").e("data is loading")
                        }

                        is Result.Success -> {
                            Timber.tag("data").e(resonse.data.location.country)
                        }
                    }

                }
        }

    }

    fun showAutoComplete(){

    }


}

sealed interface UiEvents
sealed class AutoCompleteEvents : UiEvents
data class ShowAutoCompleteData(
    val countryName: String, val cityName: String, val lat: Double, val lon: Double
) : AutoCompleteEvents()

data class ErrorOnAutoCompete(val exception: Exception) : AutoCompleteEvents()
data class ClickOnAutoComplete(val showAutoCompleteData: ShowAutoCompleteData) :
    AutoCompleteEvents()

object LoadingAutoComplete : AutoCompleteEvents()

sealed class ShowWeatherEvents : UiEvents
object LoadingWeatherData : ShowWeatherEvents()
object AskingLocationPermission : ShowWeatherEvents()
data class ShowWeatherData(val data: CurrentWeather) : ShowWeatherEvents()
data class Error(val ex: Exception) : ShowWeatherEvents()