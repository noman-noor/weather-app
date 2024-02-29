package com.supertal.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.supertal.weatherapp.databinding.ActivityMainBinding
import com.supertal.weatherapp.home.HomeViewModel
import com.supertal.weatherapp.utils.GpsUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: ActivityMainBinding
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        locationInitialization()


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
        viewModel.loadCurrentWeather(location.latitude.toString() + "," + location.longitude.toString())
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