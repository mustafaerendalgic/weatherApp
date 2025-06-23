package com.example.weather.UI

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.R
import com.example.weather.data.HourlyList
import com.example.weather.util.getFontId


class ViewHolderHourly(item : View) : RecyclerView.ViewHolder(item){
    val saat : TextView
    val hava_derece : TextView
    val animasyon : LottieAnimationView
    val cl : ConstraintLayout
    val im2 : ImageView
    val hissedilen : TextView
    init {
        saat = item.findViewById(R.id.design_saat)
        hava_derece = item.findViewById(R.id.sicaklik)
        animasyon = item.findViewById(R.id.saatlik_animasyon)
        cl = item.findViewById(R.id.includings)
        im2 = item.findViewById(R.id.bulutimage2)
        hissedilen = item.findViewById(R.id.sicaklik_apparent)
    }
}



class AdapterHourly(private var sel_color : String, val ctx : Context, private var font : String) : ListAdapter<HourlyList, ViewHolderHourly>(DiffCallback()){

    fun backGUpdate(color : String){
        sel_color = color
        notifyDataSetChanged()
    }

    fun fontUpdate(fontName : String){
        font = fontName
        notifyDataSetChanged()
    }

    fun changeTheFont(holder: ViewHolderHourly){
        val textViews = listOf(holder.saat, holder.hava_derece, holder.hissedilen)
        var fontid = getFontId(ctx, font)
        fontid?.let {
            for (tv in textViews){
                if(font == "annie")
                    tv.setTypeface(fontid, Typeface.BOLD)
                else
                    tv.setTypeface(fontid, Typeface.NORMAL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHourly {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hourly_design, parent, false)
        return ViewHolderHourly(v)
    }

    override fun onBindViewHolder(holder: ViewHolderHourly, position: Int) {
        val item = getItem(position)
        holder.im2.visibility = View.INVISIBLE
        holder.animasyon.visibility = View.VISIBLE
        holder.hava_derece.text = item.temperature_2m.toString() + " °C"
        holder.hissedilen.text = "His. " + item.apparent_temperature.toString() +  " °C"
        holder.saat.text = item.time.drop(11)

        var animasyon_ismi = "gunesmini2.json"

        if(item.time.drop(11).split(":")[0].toDouble() > 19 || item.time.drop(11).split(":")[0].toDouble() < 7){
            animasyon_ismi = "gecemoon.json"
        }

        else if(item.rain > 0 || item.showers > 0){
            animasyon_ismi = "rainmini.json"
        }

        else if (item.cloud_cover > 90){
            holder.animasyon.visibility = View.INVISIBLE
            holder.im2.visibility = View.VISIBLE

        }

        if(holder.animasyon.visibility == View.VISIBLE) {
            holder.animasyon.setAnimation(animasyon_ismi)
            holder.animasyon.playAnimation()

        }
        holder.cl.setBackgroundColor(Color.parseColor(sel_color))
        changeTheFont(holder)
    }
}
