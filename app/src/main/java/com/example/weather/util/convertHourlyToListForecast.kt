package com.example.weather.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weather.data.DailyForecast
import com.example.weather.data.WeatherResponse


@RequiresApi(Build.VERSION_CODES.O)
fun convertHourlyToListForecast(weatherData: WeatherResponse, ctx: Context) : List<DailyForecast>{
    val list = ArrayList<DailyForecast>()

    val temp = ArrayList<Double>()
    val ap_temp = ArrayList<Double>()
    val hum = ArrayList<Int>()
    val rain = ArrayList<Double>()
    val time = ArrayList<String>()

    var previousBound = 0
    for (i in weatherData.hourly.time.indices){
        var a: DailyForecast

        temp.add(weatherData.hourly.temperature_2m[i])
        ap_temp.add(weatherData.hourly.apparent_temperature[i])
        hum.add(weatherData.hourly.relative_humidity_2m[i])
        rain.add(weatherData.hourly.rain[i] + weatherData.hourly.showers[i])
        time.add(weatherData.hourly.time[i])

        val max_temp = temp.max().toInt()
        val min_temp = temp.min().toInt()
        val max_ap_temp = ap_temp.max().toInt()
        val min_ap_temp = ap_temp.min().toInt()
        val max_hum = hum.max()
        val min_hum = hum.min()
        val rain_intervals = getRainIntervals(time, rain)

        if((i + 1) % 24 == 0){
            val locale = ctx.resources.configuration.locales[0]
            a = DailyForecast(formatDate(weatherData.hourly.time[previousBound].substring(0,10), locale), max_temp.toString(), min_temp.toString(), max_ap_temp.toString(), min_ap_temp.toString(), max_hum.toString(), min_hum.toString(), rain_intervals, null )
            previousBound = i+1
            list.add(a)
            temp.clear()
            ap_temp.clear()
            hum.clear()
            rain.clear()
            time.clear()
        }
    }
    return list
}