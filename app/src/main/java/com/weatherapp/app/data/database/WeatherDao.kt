package com.weatherapp.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weatherapp.app.data.database.entities.WeatherEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_table ORDER BY id ASC")
    fun getWeatherList(): Flow<List<WeatherEntities>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(userEntities: WeatherEntities)
}