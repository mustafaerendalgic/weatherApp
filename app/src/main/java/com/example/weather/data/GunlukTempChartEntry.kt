package com.example.weather.data


import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData

data class GunlukTempChartEntry(
    val Temp : List<Entry>,
    val TempApp : List<Entry>
)