package com.weatherapp.app.di

import android.content.Context
import androidx.room.Room
import com.weatherapp.app.data.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        WeatherDatabase::class.java,
        "weather_database"
    ).build()

    @Singleton
    @Provides
    fun weatherDao(database: WeatherDatabase) = database.weatherData()

}