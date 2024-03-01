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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeViewModel(private val weatherService: IWeatherService) : ViewModel() {

    var job: Job? = null


    val queryString: MutableLiveData<String> = MutableLiveData("")

    private val _currentWeatherData = MutableLiveData<CurrentWeatherUiData>()
    val currentWeatherData: LiveData<CurrentWeatherUiData> = _currentWeatherData

    private val _autoCompleteResults = MutableLiveData<AutoComplete?>()
    val autoCompleteResults: LiveData<AutoComplete?> = _autoCompleteResults

    private val _uiEvent = MutableLiveData<UiEvents>()
    val uiEvents = _uiEvent

    private val _autoCompleteListVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val autoCompleteListVisibility: LiveData<Boolean> = _autoCompleteListVisibility

    private val _weatherViewVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
    val weatherViewVisibility: LiveData<Boolean> = _weatherViewVisibility


    private val _loadingAutoComplete: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingAutoComplete: LiveData<Boolean> = _loadingAutoComplete

    private val _loadingWeatherData: MutableLiveData<Boolean> = MutableLiveData(true)
    val loadingWeatherData: LiveData<Boolean> = _loadingWeatherData


    var lastQuery: String = ""

    fun loadCurrentWeather(query: String) {
        viewModelScope.launch {
            weatherService.provideCurrentWeatherUpdate(WeatherParams(query)).collect { response ->
                when (response) {
                    is Result.Error -> {
                        _loadingWeatherData.value = false
                        _uiEvent.postValue(Error(response.exception))
                        response.exception.printStackTrace()
                    }

                    Result.Loading -> {
                        _loadingWeatherData.value = true
                        _uiEvent.postValue(LoadingWeatherData)
                        Timber.tag("Loading").e("data is loading")
                    }

                    is Result.Success -> {
                        _loadingWeatherData.value = false
                        transformUiModel(response.data)
                        Timber.tag("data").e(response.data.location.country)
                    }
                }

            }
        }

    }

    fun onFocusChange(hasFocus: Boolean) {
        _autoCompleteListVisibility.value = hasFocus
        _weatherViewVisibility.value = !hasFocus
    }

    private fun transformUiModel(data: CurrentWeather) {
        val dateFormat = SimpleDateFormat("dd MMMM, hh:mm a", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone(data.location.tzId)
        val date = Date()
        //date.time = data.location.localtimeEpoch * 1000.toLong()

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
            cloudCover = data.current.cloud.toString() + "%",
            windSpeed = data.current.windKph.toString()


        )
        _currentWeatherData.postValue(uiData)
    }

    fun onRefresh() {
        _loadingWeatherData.value = true
        loadCurrentWeather(lastQuery)
    }

    fun getAutCompleteData(query: String) {
        Timber.tag("Query").e(query)
        job = viewModelScope.launch {
            weatherService.autoComplete(query).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _loadingAutoComplete.value = false
                        result.exception.printStackTrace()
                    }

                    Result.Loading -> {
                        _loadingAutoComplete.value = true
                    }

                    is Result.Success -> {
                        _loadingAutoComplete.value = false
                        _autoCompleteResults.value = result.data
                        Timber.tag("Results").e(result.data.toString())

                    }
                }

            }
        }
    }

    fun changeUnit(unit: String) {
        val data = currentWeatherData.value
        data?.apply {
            this.unit = unit
            _currentWeatherData.value = this
        }


    }

    fun updateVisibility(autoComplete: Boolean, weather: Boolean) {
        _autoCompleteListVisibility.value = autoComplete
        _weatherViewVisibility.value = weather
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