package com.example.weather.data

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val rain: String,
    val showers: String,
    val snowfall: String
)