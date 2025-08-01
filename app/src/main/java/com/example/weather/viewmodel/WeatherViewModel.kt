package com.example.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.DAO.DaoForWeather
import com.example.weather.data.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val dao: DaoForWeather) : ViewModel() {

    private var _weatherResponse : WeatherResponse? = null
    var weatherResponse : WeatherResponse? = _weatherResponse

    fun storeWeatherResponseInLocalVariable(weatherResponse1: WeatherResponse){
        _weatherResponse = weatherResponse1
    }

    fun setWeatherToRoom(weatherResponse1: WeatherResponse){
        viewModelScope.launch {
            dao.insertWeatherResponse(weatherResponse1)
            storeWeatherResponseInLocalVariable(weatherResponse1)
        }
    }

    fun getTheWeatherFromRoomAndStoreIt(){
        viewModelScope.launch {
            dao.getResponse()?.let { storeWeatherResponseInLocalVariable(it) }
        }
    }

    suspend fun getWeather(): WeatherResponse? {
        return dao.getResponse()
    }

}