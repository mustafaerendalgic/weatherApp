package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class viewholder_hourly(item : View) : RecyclerView.ViewHolder(item){
    val saat : TextView
    val hava_derece : TextView
    init {
        saat = item.findViewById(R.id.design_saat)
        hava_derece = item.findViewById(R.id.sicaklik)
    }
}

class Adapter_hourly() : ListAdapter<HourlyUnits, viewholder_hourly>(Diffcallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder_hourly {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hourly_design, parent, false)
        return viewholder_hourly(v)
    }

    override fun onBindViewHolder(holder: viewholder_hourly, position: Int) {
        val item = getItem(position)
        //holder.hava_derece = item
    }
}

class Diffcallback : DiffUtil.ItemCallback<HourlyUnits>(){
    override fun areItemsTheSame(oldItem: HourlyUnits, newItem: HourlyUnits): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: HourlyUnits, newItem: HourlyUnits): Boolean {
        return oldItem == newItem
    }
}