package com.example.weather.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weather.data.HourlyList
import com.example.weather.data.WeatherResponse
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
fun convertHourlyToList(weatherData: WeatherResponse, flag: Int = 0) : List<HourlyList>{
    val list = ArrayList<HourlyList>()
    var hour = LocalTime.now().hour //api 26

    if(flag == 1){
        hour = 0
    }

    for (i in hour until (hour+24)){
        val a = HourlyList(weatherData.hourly.time[i], weatherData.hourly.temperature_2m[i].toInt(), weatherData.hourly.relative_humidity_2m[i], weatherData.hourly.rain[i], weatherData.hourly.showers[i], weatherData.hourly.snowfall[i], weatherData.hourly.cloud_cover[i], weatherData.hourly.apparent_temperature[i].toInt(), weatherData.hourly.wind_speed_10m[i], weatherData.hourly.wind_direction_10m[i])
        list.add(a)
    }

    return list
}