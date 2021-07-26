package com.weatherapp.app

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.weatherapp.app.adapter.ListAdapter
import com.weatherapp.app.databinding.ActivityMainBinding
import com.weatherapp.app.viewModel.MainViewModel
import com.weatherapp.app.util.ExtensionFunctions.showToast
import com.weatherapp.app.util.NetworkListener
import com.weatherapp.app.util.NetworkResult
import com.weatherapp.app.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mainViewModel: MainViewModel

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private lateinit var networkListener: NetworkListener

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setUpRecyclerView()

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.txtDate.text = formatter.format(Date())

        mainViewModel.readBackOnline.observe(this, {
            mainViewModel.backOnline = it
            getWeather()
        })

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@MainActivity)

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@MainActivity)
                .collect { status ->
                    mainViewModel.networkStatus = status
                    mainViewModel.showNetworkStatus()
                    readDatabase()
                }
        }
    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.recycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun getWeather() {
        if (hasLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location = it.result
                if (location != null) {
                    val geoCoder = Geocoder(this@MainActivity)
                    val currentLocation = geoCoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    getCurrentWeather(location.latitude.toString(), location.longitude.toString())
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.getWeather.observeOnce(this@MainActivity, { database ->
                if (database.isNotEmpty()) {
                    adapter.setData(database.first().weatherResponse)
                } else {
                    getWeather()
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCurrentWeather(lat: String, long: String) {
        mainViewModel.getCurrentWeatherData(lat, long)
        mainViewModel.weatherResponse.observe(this, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { weatherResponse ->
                        adapter.setData(weatherResponse)
                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    showToast(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
            }
        })
    }


    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.getWeather.observe(this@MainActivity, { database ->
                if (database.isNotEmpty()) {
                    adapter.setData(database.first().weatherResponse)
                }
            })
        }
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this@MainActivity).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        showToast("Permission Granted!")
    }
}