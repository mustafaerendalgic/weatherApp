package com.example.weather.data

import com.github.mikephil.charting.data.LineDataSet

data class HaftalikDataSet(
    val tempMax: LineDataSet,
    val tempMin: LineDataSet,
    val dataSun: LineDataSet
)