package com.example.weather.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

fun saveColorToSharedPref(context: Context, color: String) {
    val sharedPref = context.getSharedPreferences("renk_shared", MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("renk_shared", color)
    editor.apply()
}

fun getColorFromSharedPref(context: Context): String? {
    val sharedPref = context.getSharedPreferences("renk_shared", MODE_PRIVATE)
    return sharedPref.getString("renk_shared", "#869feb") //"#6ec29a"
}

fun saveDataToSharedPRef(context: Context, sharedName: String, dataName: String, data: String) {
    val sharedPref = context.getSharedPreferences(sharedName, MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(dataName, data)
    editor.apply()
}

fun getDataFromSharedPref(context: Context, sharedName: String, dataName: String): String {
    val sharedPref = context.getSharedPreferences(sharedName, MODE_PRIVATE)
    return sharedPref.getString(dataName, "empty") ?: "empty"
}

fun saveLocToSharedPref(context: Context, latitude: Double, longitude: Double) {
    val sharedPref = context.getSharedPreferences("location_prefs", MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("latitude", latitude.toString())
    editor.putString("longitude", longitude.toString())
    editor.apply()
}

fun getLocFromSharedPref(context: Context): Pair<Double, Double>? {
    val sharedPref = context.getSharedPreferences("location_prefs", MODE_PRIVATE)
    val lat = sharedPref.getString("latitude", null)?.toDoubleOrNull()
    val lon = sharedPref.getString("longitude", null)?.toDoubleOrNull()
    return if (lat != null && lon != null) Pair(lat, lon) else null
}
