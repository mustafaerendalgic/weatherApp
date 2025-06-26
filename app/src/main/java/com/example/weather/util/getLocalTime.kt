package com.example.weather.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getLocalTime() : String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(calendar.time)
}