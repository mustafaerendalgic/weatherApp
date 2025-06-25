package com.example.weather.data

data class Current(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val is_day : Int,
    val cloud_cover : Double,
    val wind_speed_10m : Double,
    val precipitation : Double,
    val pressure_msl : Double,
    val wind_direction_10m : Double,
    val apparent_temperature : Double,
    val surface_pressure : Double,
    val wind_gusts_10m : Double
)