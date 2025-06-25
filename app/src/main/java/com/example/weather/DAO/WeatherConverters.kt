package com.example.weather.DAO

import androidx.room.TypeConverter
import com.example.weather.data.Current
import com.example.weather.data.CurrentUnits
import com.example.weather.data.Hourly
import com.example.weather.data.HourlyUnits
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class WeatherConverters {
    val gson = Gson()

    @TypeConverter
    fun fromCurrent(current: Current): String = gson.toJson(current)

    @TypeConverter
    fun toCurrent(data: String): Current = gson.fromJson(data, object: TypeToken<Current>() {}.type)

    @TypeConverter
    fun fromHourly(hourly: Hourly) : String = gson.toJson(hourly)

    @TypeConverter
    fun toHourly(data: String): Hourly = gson.fromJson(data, object: TypeToken<Hourly>() {}.type)

    @TypeConverter
    fun fromCurrentUnits(units: CurrentUnits): String = gson.toJson(units)

    @TypeConverter
    fun toCurrentUnits(data: String): CurrentUnits = gson.fromJson(data, object : TypeToken<CurrentUnits>() {}.type)

    @TypeConverter
    fun fromHourlyUnits(units: HourlyUnits): String = gson.toJson(units)

    @TypeConverter
    fun toHourlyUnits(data: String): HourlyUnits = gson.fromJson(data, object : TypeToken<HourlyUnits>() {}.type)

}