package com.example.weather.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.airbnb.lottie.animation.content.Content
import com.example.weather.data.ForecastChart
import com.example.weather.data.WeatherResponse


@RequiresApi(Build.VERSION_CODES.O)
fun convertHourlyToListForecastChart(weatherData: WeatherResponse, ctx: Context) : List<ForecastChart>{
    val list = ArrayList<ForecastChart>()

    val temp = ArrayList<Double>()
    val ap_temp = ArrayList<Double>()

    val rain = ArrayList<Double>()
    val time = ArrayList<String>()

    var previousBound = 0
    for (i in weatherData.hourly.time.indices){
        var a: ForecastChart

        temp.add(weatherData.hourly.temperature_2m[i])
        ap_temp.add(weatherData.hourly.apparent_temperature[i])

        rain.add(weatherData.hourly.rain[i] + weatherData.hourly.showers[i])
        time.add(weatherData.hourly.time[i])

        val max_temp = temp.max().toInt()
        val min_temp = temp.min().toInt()
        val rain_intervals = getRainIntervals(time, rain)

        if((i + 1) % 24 == 0){
            val locale = ctx.resources.configuration.locales[0]
            a = ForecastChart(formatDate(weatherData.hourly.time[previousBound].substring(0,10), locale), max_temp.toString(), min_temp.toString(), if(rain_intervals.isNotEmpty()) 1 else 0)
            previousBound = i+1
            list.add(a)
            temp.clear()
            ap_temp.clear()
            rain.clear()
            time.clear()
        }
    }
    return list
}