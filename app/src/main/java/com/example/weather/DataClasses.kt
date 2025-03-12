package com.example.weather

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val current_units: CurrentUnits,
    val hourly_units: HourlyUnits,
    val current: Current,
    val hourly: Hourly
)

data class CurrentUnits(
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val rain: String,
    val showers: String,
    val snowfall: String
)

data class Current(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val rain: Double,
    val showers: Double,
    val snowfall: Double
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val rain: String,
    val showers: String,
    val snowfall: String
)

data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Int>,
    val rain: List<Double>,
    val showers: List<Double>,
    val snowfall: List<Double>
)
