package com.example.weather.retrofit

import com.example.weather.data.WeatherResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherResponseInterface{
    @GET("v1/forecast")
    fun getWeather(
        @Query("latitude") lat : Double,
        @Query("longitude") long : Double,
        @Query("hourly") hrly : String = "temperature_2m,relative_humidity_2m,rain,showers,snowfall,cloud_cover,apparent_temperature,wind_speed_10m,wind_direction_10m,uv_index",
        @Query("current") curr : String = "temperature_2m,relative_humidity_2m,rain,showers,snowfall,is_day,cloud_cover,wind_speed_10m,precipitation,pressure_msl,wind_direction_10m,apparent_temperature,surface_pressure,wind_gusts_10m,is_day",
        @Query("timezone") timezone: String = "auto"
    ) : Call<WeatherResponse>
}

object RetrApi{
    private val base_url = "https://api.open-meteo.com/"

    val RetrofitBuilder : WeatherResponseInterface by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherResponseInterface::class.java)
    }


}