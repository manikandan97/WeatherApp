package com.weatherapp.app.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weatherapp.app.model.WeatherResponse

class WeatherTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun weatherResponseToString(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun stringToWeather(data: String): WeatherResponse {
        val listType = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(data, listType)
    }

}