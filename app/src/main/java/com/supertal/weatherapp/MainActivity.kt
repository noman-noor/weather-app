package com.supertal.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.supertal.weatherapp.core.dataModels.AutoCompleteItem
import com.supertal.weatherapp.core.dataModels.ForecastParams
import com.supertal.weatherapp.databinding.ActivityMainBinding
import com.supertal.weatherapp.forecast.ForecastAdapter
import com.supertal.weatherapp.home.AutCompleteAdapter
import com.supertal.weatherapp.home.AutoCompleteClickListener
import com.supertal.weatherapp.home.HomeViewModel
import com.supertal.weatherapp.utils.GpsUtils
import com.supertal.weatherapp.utils.getQueryTextChangeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: ActivityMainBinding
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private val forecastAdapter: ForecastAdapter = ForecastAdapter()
    private val adapter: AutCompleteAdapter =
        AutCompleteAdapter(object : AutoCompleteClickListener {
            override fun onClick(data: AutoCompleteItem) {
                hideKeyboard()
                viewModel.loadCurrentWeather(data.name)
                viewModel.getForecastData(ForecastParams(data.name, 15))
                binding.queryInput.clearFocus()
                viewModel.updateVisibility(autoComplete = false, weather = true)
            }

        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        locationInitialization()
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = adapter
        viewsCallback()
        observerValues()

    }

    private fun observerValues() {
        viewModel.autoCompleteResults.observe(this) { data ->
            if (data != null) adapter.setList(data)
        }

        viewModel.forecastData.observe(this) { data ->
            if (data != null) forecastAdapter.setList(data)
        }
        viewModel.error.observe(this){


        }
    }

    private fun showSnackBar(){
        val snackbar = Snackbar
            .make(binding.mainLayout, getString(R.string.error_load_data), Snackbar.LENGTH_INDEFINITE)
            .setAction("Try gain") {
                viewModel.onRefresh()
            }

        snackbar.show()
    }

    private fun hideKeyboard() {
        val imm =
            binding.queryInput.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val hideSoftInputFromWindow = imm.hideSoftInputFromWindow(binding.queryInput.windowToken, 0)
    }

    private fun viewsCallback() {
        binding.forecastListView.adapter = forecastAdapter
        binding.swiperefresh.isRefreshing = false
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadCurrentWeather("karachi")

        }

        CoroutineScope(Dispatchers.Main).launch {
            binding.queryInput.getQueryTextChangeStateFlow().filter { query ->
                if (viewModel.queryString.value.isNullOrBlank()) {
                    viewModel.updateVisibility(autoComplete = false, weather = true)
                    adapter.setList(emptyList())
                    binding.queryInput.clearFocus()
                    return@filter false
                } else {
                    viewModel.updateVisibility(autoComplete = true, weather = false)
                    return@filter true
                }

            }.debounce(1000).distinctUntilChanged().flatMapLatest { query ->
                flow {
                    emit(query)
                }
            }.flowOn(Dispatchers.Main).collect { result ->
                viewModel.job?.cancel()
                viewModel.getAutCompleteData(viewModel.queryString.value ?: "")

            }
        }


    }

    private fun locationInitialization() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        mFusedLocationClient.removeLocationUpdates(locationCallback)
                        loadData(location)
                        break
                    }
                }
            }
        }
        checkLocation()
    }

    private fun loadData(location: Location) {
        viewModel.lastQuery = location.latitude.toString() + "," + location.longitude.toString()
        viewModel.loadCurrentWeather(viewModel.lastQuery)
        viewModel.getForecastData(ForecastParams(viewModel.lastQuery, 15))
    }


    private fun checkLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), GpsUtils.LOCATION_REQUEST
            )
        } else {

            mFusedLocationClient.lastLocation.addOnSuccessListener(
                this
            ) { location: Location? ->
                if (location != null) {
                    wayLatitude = location.latitude
                    wayLongitude = location.longitude
                    loadData(location)
                } else {
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest, locationCallback, null
                    )
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.lastLocation.addOnSuccessListener(
                        this
                    ) { location: Location? ->
                        if (location != null) {
                            wayLatitude = location.latitude
                            wayLongitude = location.longitude
                            loadData(location)
                        } else {
                            mFusedLocationClient.requestLocationUpdates(
                                locationRequest, locationCallback, null
                            )
                        }
                    }
                } else {

                }
            }
        }
    }
}