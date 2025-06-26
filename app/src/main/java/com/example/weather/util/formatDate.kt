package com.example.weather.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: String): String {
    val localDate = LocalDate.parse(date)
    val formatter = DateTimeFormatter.ofPattern("d MMMM, E", Locale("tr"))
    return localDate.format(formatter)
}