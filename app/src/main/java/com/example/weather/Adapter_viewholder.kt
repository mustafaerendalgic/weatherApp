package com.example.weather

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide

class viewholder_hourly(item : View) : RecyclerView.ViewHolder(item){
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

class viewholder_hourly_detailed(item : View) : RecyclerView.ViewHolder(item){
    val saat : TextView
    val hava_derece : TextView
    val animasyon : LottieAnimationView
    val cl : ConstraintLayout
    val im1 : ImageView
    val hissedilen : TextView
    val nem : TextView
    val ruzgarhiz : TextView
    val ruzgaryon : TextView
    init {
        saat = item.findViewById(R.id.design_saat2)
        hava_derece = item.findViewById(R.id.sicaklik2)
        animasyon = item.findViewById(R.id.saatlik_animasyon2)
        cl = item.findViewById(R.id.includings2)
        im1 = item.findViewById(R.id.bulutimage22)
        hissedilen = item.findViewById(R.id.sicaklik_apparent2)
        nem = item.findViewById(R.id.nem_hourly2)
        ruzgarhiz = item.findViewById(R.id.ruzgarhizi2)
        ruzgaryon = item.findViewById(R.id.ruzgaryonu2)
    }
}

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

fun getFontId(ctx : Context, font: String) : Typeface? {
    return when(font){
        "indie" -> ResourcesCompat.getFont(ctx, R.font.indie)
        "nb" -> ResourcesCompat.getFont(ctx, R.font.nb)
        "annie" -> ResourcesCompat.getFont(ctx, R.font.annie)
        "single" -> ResourcesCompat.getFont(ctx, R.font.single)
        "zilla" -> ResourcesCompat.getFont(ctx, R.font.zilla)
        "inter" -> ResourcesCompat.getFont(ctx, R.font.inter)
        else -> ResourcesCompat.getFont(ctx, R.font.annie)
    }
}

class Adapter_hourly(private var sel_color : String, val ctx : Context, private var font : String) : ListAdapter<hourly_list, viewholder_hourly>(Diffcallback()){

    fun backGUpdate(color : String){
        sel_color = color
        notifyDataSetChanged()
    }

    fun fontUpdate(fontName : String){
        font = fontName
        notifyDataSetChanged()
    }

    fun changeTheFont(holder: viewholder_hourly){
        val textViews = listOf(holder.saat, holder.hava_derece, holder.hissedilen)
        var fontid = getFontId(ctx, font)
        fontid?.let {
            for (tv in textViews){
                tv.typeface = fontid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder_hourly {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hourly_design, parent, false)
        return viewholder_hourly(v)
    }

    override fun onBindViewHolder(holder: viewholder_hourly, position: Int) {
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

class Adapter_hourly_detailed(private var sel_color: String, val ctx : Context, private var font: String) : ListAdapter<hourly_list, viewholder_hourly_detailed>(Diffcallback()){

    fun backGUpdate(color : String){
        sel_color = color
        notifyDataSetChanged()
    }

    fun fontUpdate(fontName : String){
        font = fontName
        notifyDataSetChanged()
    }

    fun changeTheFont(holder: viewholder_hourly_detailed){
        val textViews = listOf(holder.saat, holder.hava_derece, holder.hissedilen, holder.nem, holder.ruzgaryon, holder.ruzgarhiz)
        var fontid = getFontId(ctx, font)
        fontid?.let {
            for (tv in textViews){
                tv.typeface = fontid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder_hourly_detailed {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hourly_design_detailed, parent, false)
        return viewholder_hourly_detailed(v)
    }

    override fun onBindViewHolder(holder: viewholder_hourly_detailed, position: Int) {
        val item = getItem(position)
        holder.im1.visibility = View.INVISIBLE
        holder.animasyon.visibility = View.VISIBLE
        holder.hava_derece.text = "Sıcaklık: "+ item.temperature_2m.toString() + " °C"
        holder.hissedilen.text = "Hissedilen: " + item.apparent_temperature.toString() +  " °C"
        holder.saat.text = item.time.drop(11)
        holder.nem.text = "Nem: " + item.relative_humidity_2m.toString() + "%"
        holder.ruzgarhiz.text = "Rüz. Hızı: " + item.wind_speed_10m.toString() + " km/h"
        holder.ruzgaryon.text = "Rüz. Yönü: " + item.wind_direction_10m.toString() + "°"

        var animasyon_ismi = "gunesmini2.json"

        if(item.time.drop(11).split(":")[0].toDouble() > 19 || item.time.drop(11).split(":")[0].toDouble() < 7){
            animasyon_ismi = "gecemoon.json"
        }

        else if(item.rain > 0 || item.showers > 0){
            animasyon_ismi = "rainmini.json"
        }

        else if (item.cloud_cover > 90){
            holder.animasyon.visibility = View.INVISIBLE
            holder.im1.visibility = View.VISIBLE
        }

        if(holder.animasyon.visibility == View.VISIBLE) {

            holder.animasyon.setAnimation(animasyon_ismi)
            holder.animasyon.playAnimation()

        }
        holder.cl.setBackgroundColor(Color.parseColor(sel_color))
        changeTheFont(holder)
    }

}

class AdapterForecast(private var color: String, val ctx: Context, private var font: String) : ListAdapter<DailyForecast, forecast_vh>(Diffcallback_forecast()){
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
                tv.typeface = fontid
            }
        }
    }

    override fun onBindViewHolder(holder: forecast_vh, position: Int) {
        val item = getItem(position)
        holder.frc_yagmurlu.visibility = View.INVISIBLE
        holder.frc_gunesli.visibility = View.VISIBLE
        holder.yagmur_aralik.visibility = View.INVISIBLE
        holder.gunes_aralik.visibility = View.VISIBLE
        holder.arkap.setBackgroundColor(Color.parseColor(color))

        holder.frc_sicaklik.text = "Sıcaklık: " + item.max_temp + " °C / " + item.min_temp + " °C"
        holder.frc_hiss_sicaklik.text = "Hissedilen Sıcaklık: " + item.his_max_temp + " °C / " + item.his_min_temp + " °C"
        holder.frc_nem.text = "Nem(Max./Min.): " + item.max_nem + "% / " + item.min_nem + "%"
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
                mesaj = "Bugün çoğunlukla yağmurlu görünüyor. Şemsiyeni unutma!   ☂\uFE0F"
            else if (yagmur_zamani >= 3)
                mesaj = "Bugün yağmur yağabilir. Şemsiyeni yanına al!   ☂\uFE0F"
            else
                mesaj = "Bugün biraz yağmur yağabilir!   ☂\uFE0F"
            holder.yagmur_aralik.text = mesaj


        }
        else{
            holder.yagmur_aralik.visibility = View.INVISIBLE
            holder.gunes_aralik.text = "Çoğunlukla güneşli!  \uD83C\uDF1E"
            holder.frc_yagmurlu.visibility = View.INVISIBLE
            holder.frc_gunesli.visibility = View.VISIBLE
            holder.gunes_aralik.visibility = View.VISIBLE
        }
        changeTheFont(holder)
    }

}

class Diffcallback : DiffUtil.ItemCallback<hourly_list>(){
    override fun areItemsTheSame(oldItem: hourly_list, newItem: hourly_list): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: hourly_list, newItem: hourly_list): Boolean {
        return oldItem == newItem
    }
}

class Diffcallback_forecast : DiffUtil.ItemCallback<DailyForecast>(){
    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}

class Item_Decoration(val space : Int) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if(position == 0 ){
            outRect.left = space
            outRect.right = space
        }
        else{
            outRect.right = space
        }

    }
}

fun yagmur_hesap(list: List<String>): Int {
    var total_time = 0
    for (interval in list) {
        val timeParts = interval.split(" - ")


        val startParts = timeParts[0].split(":")
        val endParts = timeParts[1].split(":")


        val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
        val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()


        total_time += endMinutes - startMinutes
    }
    return total_time
}