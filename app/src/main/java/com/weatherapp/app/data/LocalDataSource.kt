package com.weatherapp.app.data

import com.weatherapp.app.data.database.WeatherDao
import com.weatherapp.app.data.database.entities.WeatherEntities
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val weatherDao: WeatherDao
) {

    fun getWeatherData(): Flow<List<WeatherEntities>> {
        return weatherDao.getWeatherList()
    }

    suspend fun insertUser(weatherEntities: WeatherEntities) {
        weatherDao.insertData(weatherEntities)
    }

}