package com.weatherapp.app.data

import com.weatherapp.app.data.network.ApiInterface
import com.weatherapp.app.model.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getWeather(lat: String, long: String): Response<WeatherResponse>{
        return apiInterface.getWeather(lat.toDouble(), long.toDouble(), "minutely,alerts,daily", "814973c5c80c7d744c0eb0d91a56493f")
    }
}