package com.example.weather

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val current_units: CurrentUnits,
    val hourly_units: HourlyUnits,
    val current: Current,
    val hourly: Hourly
)

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

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val rain: String,
    val showers: String,
    val snowfall: String
)

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
    val wind_direction_10m: List<Int>
)

data class hourly_list(
    val time: String,
    val temperature_2m: Int,
    val relative_humidity_2m: Int,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val cloud_cover: Double,
    val apparent_temperature: Int,
    val wind_speed_10m: Double,
    val wind_direction_10m: Int
)

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

data class ForecastChart(
    val time: String,
    val max_temp: String,
    val min_temp: String,
    val yagmur: Int,
)

