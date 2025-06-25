package com.example.weather.data

data class CurrentUnits(
    val time: String, // "iso8601"
    val interval: String, // "seconds"
    val temperature_2m: String, // "°C"
    val is_day: String, // ""
    val showers: String, // "mm"
    val cloud_cover: String, // "%"
    val wind_speed_10m: String, // "km/h"
    val wind_direction_10m: String, // "°"
    val pressure_msl: String, // "hPa"
    val snowfall: String, // "cm"
    val precipitation: String, // "mm"
    val relative_humidity_2m: String, // "%"
    val apparent_temperature: String, // "°C"
    val rain: String, // "mm"
    val weather_code: String, // "wmo code"
    val surface_pressure: String, // "hPa"
    val wind_gusts_10m: String // "km/h"
)