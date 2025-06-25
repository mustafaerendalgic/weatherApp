package com.example.weather.util

fun yagmur_hesap(list: List<String>): Int {
    var total_time = 0
    for (interval in list) {
        val timeParts = interval.split(" - ")


        val startParts = timeParts[0].split(":")
        val endParts = timeParts[1].split(":")


        val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
        val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()


        total_time += endMinutes - startMinutes
    }
    return total_time
}