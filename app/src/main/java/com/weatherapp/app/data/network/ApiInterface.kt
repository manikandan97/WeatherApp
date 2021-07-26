package com.weatherapp.app.data.network

import com.weatherapp.app.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("onecall")
    suspend fun getWeather(@Query("lat") lat: Double,@Query("lon") lon: Double, @Query("exclude") exclude: String, @Query("appid") appid: String): Response<WeatherResponse>
}