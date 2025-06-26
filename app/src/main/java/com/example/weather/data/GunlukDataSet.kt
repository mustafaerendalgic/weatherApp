package com.example.weather.data

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineDataSet

data class GunlukDataSet(val dataSet: LineDataSet,
    val appDataSet: LineDataSet,
    val humDataSet: LineDataSet,
    val windDataSet: BarDataSet)