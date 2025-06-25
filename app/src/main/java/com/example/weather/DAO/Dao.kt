package com.example.weather.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.WeatherResponse

@Dao
interface DaoForWeather {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    @Query ("SELECT * FROM weatherresponse WHERE id = 0 ")
    suspend fun getResponse() : WeatherResponse?
}