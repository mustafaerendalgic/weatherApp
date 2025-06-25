package com.example.weather.UI

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.data.DailyForecast


class DiffCallbackForecast : DiffUtil.ItemCallback<DailyForecast>(){
    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}
