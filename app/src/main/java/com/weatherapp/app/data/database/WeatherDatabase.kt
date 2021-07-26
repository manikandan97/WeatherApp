package com.weatherapp.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.weatherapp.app.data.database.entities.WeatherEntities

@Database(entities = [WeatherEntities::class], version = 1, exportSchema = false)
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherData(): WeatherDao
}