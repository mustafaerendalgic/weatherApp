package com.example.weather.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "WeatherResponse")
data class WeatherResponse(
    @PrimaryKey val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val current_units: CurrentUnits,
    val hourly_units: HourlyUnits,
    val current: Current,
    val hourly: Hourly
)