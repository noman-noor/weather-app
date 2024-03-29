package com.supertal.weatherapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supertal.weatherapp.core.Result
import com.supertal.weatherapp.core.dataModels.AutoComplete
import com.supertal.weatherapp.core.dataModels.CurrentWeather
import com.supertal.weatherapp.core.dataModels.CurrentWeatherUiData
import com.supertal.weatherapp.core.dataModels.DaySession
import com.supertal.weatherapp.core.dataModels.ForecastData
import com.supertal.weatherapp.core.dataModels.ForecastParams
import com.supertal.weatherapp.core.dataModels.ForecastUiData
import com.supertal.weatherapp.core.dataModels.WeatherParams
import com.supertal.weatherapp.core.iService.IWeatherService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(private val weatherService: IWeatherService) : ViewModel() {

    var job: Job? = null


    val queryString: MutableLiveData<String> = MutableLiveData("")

    private val _currentWeatherData = MutableLiveData<CurrentWeatherUiData>()
    val currentWeatherData: LiveData<CurrentWeatherUiData> = _currentWeatherData

    private val _autoCompleteResults = MutableLiveData<AutoComplete?>()
    val autoCompleteResults: LiveData<AutoComplete?> = _autoCompleteResults


    private val _autoCompleteListVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val autoCompleteListVisibility: LiveData<Boolean> = _autoCompleteListVisibility

    private val _weatherViewVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
    val weatherViewVisibility: LiveData<Boolean> = _weatherViewVisibility


    private val _loadingAutoComplete: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingAutoComplete: LiveData<Boolean> = _loadingAutoComplete

    private val _loadingWeatherData: MutableLiveData<Boolean> = MutableLiveData(true)
    val loadingWeatherData: LiveData<Boolean> = _loadingWeatherData

    private val _loadingForecastData: MutableLiveData<Boolean> = MutableLiveData(true)
    val loadingForecastData: LiveData<Boolean> = _loadingForecastData


    private val _forecastData: MutableLiveData<List<ForecastUiData>> = MutableLiveData()
    val forecastData: LiveData<List<ForecastUiData>> = _forecastData

    private val _error: MutableLiveData<Result.Error> = MutableLiveData()
    val error: LiveData<Result.Error> = _error


    var lastQuery: String = ""

    fun loadCurrentWeather(query: String) {
        viewModelScope.launch {
            weatherService.provideCurrentWeatherUpdate(WeatherParams(query)).collect { response ->
                when (response) {
                    is Result.Error -> {
                        _loadingWeatherData.value = false
                        _error.value = response

                    }

                    Result.Loading -> {
                        _loadingWeatherData.value = true
                    }

                    is Result.Success -> {
                        _loadingWeatherData.value = false
                        transformUiModel(response.data)
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
        val time = LocalDateTime.now(ZoneId.of(data.location.tzId))
        val displayTime =
            time.format(DateTimeFormatter.ofPattern("dd MMMM, hh:mm a", Locale.ENGLISH))

        val daySession = when (time.hour) {
            in 0..6 -> DaySession.NIGHT
            in 6..11 -> DaySession.MORNING
            in 12..16 -> DaySession.DAY
            in 17..24 -> DaySession.EVENING
            else -> DaySession.MORNING

        }

        val uiData = CurrentWeatherUiData(
            location = data.location.name + "," + data.location.country,
            time = displayTime,
            weatherImageUrl = data.current.condition.icon,
            welcomeMessage = daySession.message,
            tempC = data.current.tempC,
            tempF = data.current.tempF,
            feelsLikeTempC = data.current.feelslikeC,
            feelsLikeTempF = data.current.feelslikeF,
            cloudCover = data.current.cloud.toString() + "%",
            windSpeed = data.current.windKph.toString(),
            daySession = daySession

        )
        _currentWeatherData.postValue(uiData)
    }

    fun onRefresh() {
        _loadingWeatherData.value = true
        loadCurrentWeather(lastQuery)
        getForecastData(ForecastParams(lastQuery, 15))
    }

    fun getAutCompleteData(query: String) {
        Timber.tag("Query").e(query)
        job = viewModelScope.launch {
            weatherService.autoComplete(query).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _loadingAutoComplete.value = false
                        _error.value = result
                    }

                    Result.Loading -> {
                        _loadingAutoComplete.value = true
                    }

                    is Result.Success -> {
                        _loadingAutoComplete.value = false
                        _autoCompleteResults.value = result.data

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


    fun getForecastData(params: ForecastParams) {
        viewModelScope.launch {
            weatherService.forecastData(params).collect { response ->
                when (response) {
                    is Result.Error -> {
                        _loadingForecastData.value = false
                        _error.value = response

                    }

                    Result.Loading -> {
                        _loadingForecastData.value = true

                    }

                    is Result.Success -> {
                        _loadingForecastData.value = false
                        transformForecastData(response.data)
                    }
                }

            }
        }

    }

    private fun dateFormat(date: String): String {
        val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val dateFormatOutput = SimpleDateFormat("EEEE d MMMM", Locale.ENGLISH)
        return dateFormatOutput.format(dateFormatInput.parse(date) ?: "")

    }

    private fun transformForecastData(data: ForecastData) {
        val uiData = mutableListOf<ForecastUiData>()
        data.forecast.forecastDay.forEach {
            uiData.add(
                ForecastUiData(
                    date = dateFormat(it.date),
                    message = it.day.condition.text,
                    minTemp = it.day.mintempC.toInt().toString(),
                    maxTemp = it.day.maxtempC.toInt().toString(),
                    imageUrl = it.day.condition.icon
                )
            )
        }
        _forecastData.value = uiData

    }


}





