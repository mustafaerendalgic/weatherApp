package com.example.weather.data

data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Int>,
    val rain: List<Double>,
    val showers: List<Double>,
    val snowfall: List<Double>,
    val cloud_cover: List<Double>,
    val apparent_temperature: List<Double>,
    val wind_speed_10m: List<Double>,
    val wind_direction_10m: List<Int>,
    val uv_index: List<Float>
)