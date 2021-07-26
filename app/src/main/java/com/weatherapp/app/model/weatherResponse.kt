package com.weatherapp.app.model

data class WeatherResponse(
	val current: Current,
	val timezone: String,
	val timezoneOffset: Int,
	val lon: Double,
	val hourly: List<HourlyItem>,
	val lat: Double
)

data class WeatherItem(
	val icon: String,
	val description: String,
	val main: String,
	val id: Int
)

data class Rain(
	val jsonMember1h: Double
)

data class HourlyItem(
	val temp: Double,
	val visibility: Int,
	val uvi: Double,
	val pressure: Int,
	val clouds: Int,
	val feelsLike: Double,
	val windGust: Double,
	val dt: Long,
	val pop: Double,
	val windDeg: Int,
	val dewPoint: Double,
	val weather: List<WeatherItem>,
	val humidity: Int,
	val windSpeed: Double,
	val rain: Rain
)

data class Current(
	val sunrise: Int,
	val temp: Double,
	val visibility: Int,
	val uvi: Double,
	val pressure: Int,
	val clouds: Int,
	val feelsLike: Double,
	val windGust: Double,
	val dt: Int,
	val windDeg: Int,
	val dewPoint: Double,
	val sunset: Int,
	val weather: List<WeatherItem>,
	val humidity: Int,
	val windSpeed: Double
)

