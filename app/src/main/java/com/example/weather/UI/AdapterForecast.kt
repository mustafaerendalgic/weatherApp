package com.example.weather.UI

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.R
import com.example.weather.data.DailyForecast
import com.example.weather.util.getFontId
import com.example.weather.util.getFontId
import com.example.weather.util.yagmur_hesap

class forecast_vh(item: View) : RecyclerView.ViewHolder(item){
    val tarih: TextView
    val frc_sicaklik: TextView
    val frc_hiss_sicaklik: TextView
    val frc_nem: TextView
    val frc_gunesli: LottieAnimationView
    val frc_yagmurlu: LottieAnimationView
    val yagmur_aralik: TextView
    val gunes_aralik: TextView
    val arkap: ConstraintLayout
    init {
        tarih = item.findViewById(R.id.tarih_forecast)
        frc_sicaklik = item.findViewById(R.id.sicaklik_forecast)
        frc_hiss_sicaklik = item.findViewById(R.id.hiss_sicaklik_forecast)
        frc_nem = item.findViewById(R.id.nem_forecast)
        frc_gunesli = item.findViewById(R.id.gunesli_vakitler)
        frc_yagmurlu = item.findViewById(R.id.yagmurlu_vakitler)
        yagmur_aralik = item.findViewById(R.id.yagmur_aralik)
        gunes_aralik = item.findViewById(R.id.gunes_aralik)
        arkap = item.findViewById(R.id.arkapl)
    }
}

class AdapterForecast(private var color: String, val ctx: Context, private var font: String) : ListAdapter<DailyForecast, forecast_vh>(DiffCallbackForecast()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): forecast_vh {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.forecast_design, parent, false)
        return forecast_vh(v)
    }

    fun backGUpdate(color1: String){
        color = color1
        notifyDataSetChanged()
    }

    fun fontUpdate(fontName : String){
        font = fontName
        notifyDataSetChanged()
    }

    fun changeTheFont(holder: forecast_vh){
        val textViews = listOf(holder.tarih, holder.frc_nem, holder.frc_sicaklik, holder.frc_hiss_sicaklik, holder.yagmur_aralik, holder.gunes_aralik)
        var fontid = getFontId(ctx, font)
        fontid?.let {
            for (tv in textViews){
                if(font.trim().lowercase() == "annie")
                    tv.setTypeface(fontid, Typeface.BOLD)
                else
                    tv.setTypeface(fontid, Typeface.NORMAL)
            }
        }
    }

    override fun onBindViewHolder(holder: forecast_vh, position: Int) {
        if(font == "empty")
            font = "annie"
        val item = getItem(position)
        holder.frc_yagmurlu.visibility = View.INVISIBLE
        holder.frc_gunesli.visibility = View.VISIBLE
        holder.yagmur_aralik.visibility = View.INVISIBLE
        holder.gunes_aralik.visibility = View.VISIBLE
        holder.arkap.setBackgroundColor(Color.parseColor(color))

        holder.frc_sicaklik.text = ContextCompat.getString(ctx, R.string.tempLong) + ": " + item.max_temp + " °C / " + item.min_temp + " °C"
        holder.frc_hiss_sicaklik.text = ContextCompat.getString(ctx, R.string.tempAppLong) + ": " + item.his_max_temp + " °C / " + item.his_min_temp + " °C"
        holder.frc_nem.text = ContextCompat.getString(ctx, R.string.humidity) + "(Max./Min.): " + item.max_nem + "% / " + item.min_nem + "%"
        holder.tarih.text = item.time
        if(!item.yagmur.isEmpty()){
            holder.frc_gunesli.visibility = View.INVISIBLE
            holder.gunes_aralik.visibility = View.INVISIBLE
            holder.frc_yagmurlu.visibility = View.VISIBLE
            holder.yagmur_aralik.visibility = View.VISIBLE
            val intervals = item.yagmur.split(" | ")
            val yagmur_zamani = yagmur_hesap(intervals)
            var mesaj: String

            if(yagmur_zamani >= 5)
                mesaj = ContextCompat.getString(ctx, R.string.cogunluklaYagmur)
            else if (yagmur_zamani >= 3)
                mesaj = ContextCompat.getString(ctx, R.string.ortaYagmur)
            else
                mesaj = ContextCompat.getString(ctx, R.string.azYagmur)
            holder.yagmur_aralik.text = mesaj


        }
        else{
            holder.yagmur_aralik.visibility = View.INVISIBLE
            holder.gunes_aralik.text = ContextCompat.getString(ctx, R.string.cogunluklaGunesli)
            holder.frc_yagmurlu.visibility = View.INVISIBLE
            holder.frc_gunesli.visibility = View.VISIBLE
            holder.gunes_aralik.visibility = View.VISIBLE
        }

        changeTheFont(holder)
    }

}