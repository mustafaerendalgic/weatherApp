package com.example.weather.data

data class DailyForecast(
    val time: String,
    val max_temp: String,
    val min_temp: String,
    val his_max_temp: String,
    val his_min_temp: String,
    val max_nem: String,
    val min_nem: String,
    val yagmur: String,
    val gunes: String?
)