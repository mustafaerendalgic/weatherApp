package com.example.weather

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
        @Query("hourly") hrly : String = "temperature_2m,relative_humidity_2m,rain,showers,snowfall",
        @Query("current") curr : String = "temperature_2m,relative_humidity_2m,rain,showers,snowfall",
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