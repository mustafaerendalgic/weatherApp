package com.example.weather.data

data class HourlyList(
    val time: String,
    val temperature_2m: Int,
    val relative_humidity_2m: Int,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val cloud_cover: Double,
    val apparent_temperature: Int,
    val wind_speed_10m: Double,
    val wind_direction_10m: Int
)
