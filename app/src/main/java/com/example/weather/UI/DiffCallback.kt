package com.example.weather.UI

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.data.DailyForecast
import com.example.weather.data.HourlyList

class DiffCallback : DiffUtil.ItemCallback<HourlyList>(){
    override fun areItemsTheSame(oldItem: HourlyList, newItem: HourlyList): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: HourlyList, newItem: HourlyList): Boolean {
        return oldItem == newItem
    }
}