package com.example.weather.data

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

data class DetayChartInfo(
    val ruzgarBar: List<BarEntry>,
    val nemEntry: List<Entry>
)