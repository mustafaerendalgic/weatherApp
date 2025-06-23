package com.example.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.data.WeatherResponse



class WeatherViewModel : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    var weatherResponse : LiveData<WeatherResponse> = _weatherResponse

    fun setWeatherResponse(weatherResponse1: WeatherResponse){
        _weatherResponse.value = weatherResponse1
    }

}