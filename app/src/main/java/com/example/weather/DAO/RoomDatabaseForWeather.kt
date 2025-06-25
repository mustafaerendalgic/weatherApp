package com.example.weather.DAO

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.data.WeatherResponse


@Database (entities = [WeatherResponse::class], version = 1)
@TypeConverters (WeatherConverters::class)
abstract class RoomDatabaseForWeather : RoomDatabase(){
    abstract fun dao(): DaoForWeather
}