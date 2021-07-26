package com.weatherapp.app.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.weatherapp.app.data.DataStoreRepository
import com.weatherapp.app.data.Repository
import com.weatherapp.app.data.database.entities.WeatherEntities
import com.weatherapp.app.model.WeatherResponse
import com.weatherapp.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(application: Application, private val dataStoreRepository: DataStoreRepository, private val repository: Repository) : AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

    /** ROOM DATABASE */

    val getWeather: LiveData<List<WeatherEntities>> = repository.local.getWeatherData().asLiveData()

    private fun insertUser(userEntities: WeatherEntities) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertUser(userEntities)
        }

    /** RETROFIT */
    var weatherResponse: MutableLiveData<NetworkResult<WeatherResponse>> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCurrentWeatherData(lat: String, long: String) = viewModelScope.launch {
        getCurrentWeather(lat, long)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getCurrentWeather(lat: String, long: String){
        weatherResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getWeather(lat, long)
                weatherResponse.value = handleWeatherResponse(response)
                if (weatherResponse.value != null){
                    offlineCacheWeather(weatherResponse.value!!.data!!)
                }
            } catch (e: Exception) {
                weatherResponse.value = NetworkResult.Error(e.message.toString())
            }
        } else {
            weatherResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheWeather(weatherResponse: WeatherResponse) {
        val weatherEntities = WeatherEntities(weatherResponse)
        insertUser(weatherEntities)
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>): NetworkResult<WeatherResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val weatherData = response.body()
                NetworkResult.Success(weatherData!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}