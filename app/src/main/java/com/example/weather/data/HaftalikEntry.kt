package com.example.weather.data

import com.github.mikephil.charting.data.Entry

data class HaftalikEntry(
    val tempMin : List<Entry>,
    val tempMax: List<Entry>,
    val sun: List<Entry>
)