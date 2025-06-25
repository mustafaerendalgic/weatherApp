package com.example.weather.myapp

import android.content.Context
import androidx.room.Room
import com.example.weather.DAO.DaoForWeather
import com.example.weather.DAO.RoomDatabaseForWeather
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context : Context) : RoomDatabaseForWeather{
        return Room.databaseBuilder(context, RoomDatabaseForWeather::class.java, "weather_database").build()
    }

    @Provides
    fun provideDao(db: RoomDatabaseForWeather): DaoForWeather{
        return db.dao()
    }

}