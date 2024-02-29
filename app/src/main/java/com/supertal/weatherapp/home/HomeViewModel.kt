package com.supertal.weatherapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islam360.core.common.Result
import com.supertal.core.dataModels.AutoComplete
import com.supertal.core.dataModels.CurrentWeather
import com.supertal.core.dataModels.CurrentWeatherUiData
import com.supertal.core.dataModels.WeatherParams
import com.supertal.core.iService.IWeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(private val weatherService: IWeatherService) : ViewModel() {

    private val _autoComplete = MutableStateFlow(AutoComplete())
    val autoComplete: StateFlow<AutoComplete> = _autoComplete

    private val _currentWeatherData = MutableLiveData<CurrentWeatherUiData>()
    val currentWeatherData: LiveData<CurrentWeatherUiData> = _currentWeatherData

    private val _uiEvent = MutableLiveData<UiEvents>()
    val uiEvents = _uiEvent

    fun loadCurrentWeather(query: String) {
        viewModelScope.launch {

            weatherService.provideCurrentWeatherUpdate(WeatherParams(query)).collect { response ->
                when (response) {
                    is Result.Error -> {
                        _uiEvent.postValue(Error(response.exception))
                        response.exception.printStackTrace()
                    }

                    Result.Loading -> {
                        _uiEvent.postValue(LoadingWeatherData)
                        Timber.tag("Loading").e("data is loading")
                    }

                    is Result.Success -> {
                        transformUiModel(response.data)
                        Timber.tag("data").e(response.data.location.country)
                    }
                }

            }
        }

    }

    private fun transformUiModel(data: CurrentWeather) {
        val dateFormat = SimpleDateFormat("dd MMMM, hh:mm a", Locale.ENGLISH)
        val date = Date()
        date.time = data.location.localtimeEpoch * 1000.toLong()
        val greeting = when (date.hours) {
            in 6..11 -> "Good Morning"
            in 12..16 -> "Good Day"
            in 17..20 -> "Good Evening"
            else -> "Good Evening"
        }

        val uiData = CurrentWeatherUiData(
            location = data.location.country + "," + data.location.name,
            time = dateFormat.format(date),
            weatherImageUrl = data.current.condition.icon,
            welcomeMessage = greeting,
            tempC = data.current.tempC,
            tempF = data.current.tempF,
            feelsLikeTempC = data.current.feelslikeC,
            feelsLikeTempF = data.current.feelslikeF,
        )
        _currentWeatherData.postValue(uiData)
    }

    fun showAutoComplete() {

    }

    fun changeUnit(unit: String) {
        val data = currentWeatherData.value
        data?.apply {
            this.unit = unit
            _currentWeatherData.value = this
        }


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
data class ShowWeatherData(val data: CurrentWeatherUiData) : ShowWeatherEvents()
data class Error(val ex: Exception) : ShowWeatherEvents()