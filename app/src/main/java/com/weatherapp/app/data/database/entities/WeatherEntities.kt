package com.weatherapp.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherapp.app.model.HourlyItem
import com.weatherapp.app.model.Rain
import com.weatherapp.app.model.WeatherItem
import com.weatherapp.app.model.WeatherResponse

@Entity(tableName = "weather_table")
class WeatherEntities(
    var weatherResponse: WeatherResponse
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}