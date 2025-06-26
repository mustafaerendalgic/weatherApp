package com.example.weather.util

import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getAreaName(latitude: Double, longitude: Double, context: Context) : List<String>{
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        if(address!!.isNotEmpty()){
            val city = address[0].adminArea
            val district = address[0].subAdminArea
            listOf(district, city)
        }
        else{
            emptyList()
        }
    }
    catch (e: Exception){
        e.printStackTrace()
        emptyList()
    }
}