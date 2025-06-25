package com.example.weather

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.databinding.ActivityAyarlarBinding
import com.example.weather.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ayarlar : AppCompatActivity() {

    private lateinit var binding: ActivityAyarlarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAyarlarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val get_switch_info = getDataFromSharedPref(this, "switch_detay", "switch_detay")

        val switchdetay = binding.switchdetay
        switchdetay.isChecked = if(get_switch_info == "1") true else false
        switchdetay.setOnCheckedChangeListener { _, isChecked ->
            val valueToSave = if (isChecked) "1" else "0"
            saveDataToSharedPRef(this, "switch_detay", "switch_detay", valueToSave)
        }

        val imageViews = listOf(
            binding.imageView1,
            binding.imageView2,
            binding.imageView3,
            binding.imageView4,
            binding.imageView5,
            binding.imageView6,
            binding.imageView7,
            binding.imageView8,
            binding.imageView9,
            binding.imageView10,
            binding.imageView11,
            binding.imageView12,
            binding.imageView13,
            binding.imageView14,
            binding.imageView15,

        )

        val colors = listOf(
            "#c7a5d6",
            "#89a7fa",
            "#f59af2",
            "#ae7bc9",
            "#de5992",
            "#7c6fde",
            "#8f7feb",
            "#869feb",
            "#86c3eb",
            "#8dd2d9", //10
            "#91e3b8",
            "#91e396",
            "#98db81",
            "#b3db81",
            "#b3db81"
            )

        val color = getColorFromSharedPref(this)
        for (index in colors.indices){
            if(colors[index] == color){
                val colorImage = imageViews[index]
                val scale = 1.2f
                colorImage.animate().setDuration(200).scaleX(scale).scaleY(scale).start()
            }
            else{
                val colorImage = imageViews[index]
                val scale = 1f
                colorImage.animate().setDuration(200).scaleX(scale).scaleY(scale).start()
            }
        }

        val fonts = listOf(
            binding.nb,
            binding.single,
            binding.inter,
            binding.annie,
            binding.zilla,
            binding.indie
            )
        var selected = getDataFromSharedPref(this, "selected_font", "selected_font")
        if(selected == "empty")
            selected = "annie"
        var index: Int
        when(selected){
            "annie"-> index = 3
            "zilla" -> index = 4
            "inter" -> index = 2
            "single" -> index = 1
            "nb" -> index = 0
            "indie" -> index = 5
            else -> index = 0
        }
        for (i in fonts.indices){
            if(i == index){
                fonts[i].setTextColor(Color.parseColor("#7c6fde"))
            }
            else{
                fonts[i].setTextColor(Color.parseColor("#242424"))
            }
        }

        val fontStringsToSend = listOf(
            "nb",
            "single",
            "inter",
            "annie",
            "zilla",
            "indie"
        )

        for (i in fonts.indices){
            fonts[i].setOnClickListener{
                saveDataToSharedPRef(this, "selected_font", "selected_font", fontStringsToSend[i])
                val selected = getDataFromSharedPref(this, "selected_font", "selected_font")
                when(selected){
                    "annie"-> index = 3
                    "zilla" -> index = 4
                    "inter" -> index = 2
                    "single" -> index = 1
                    "nb" -> index = 0
                    "indie" -> index = 5
                    else -> index = 0
                }
                for (i in fonts.indices){
                    if(i == index){
                        fonts[i].setTextColor(Color.parseColor("#7c6fde"))
                    }
                    else{
                        fonts[i].setTextColor(Color.parseColor("#242424"))
                    }
                }
            }
        }

        for (i in imageViews.indices){
            imageViews[i].setOnClickListener {
                saveColorToSharedPref(this, colors[i])
                imageViews[i].animate().setDuration(200).scaleX(1.3f).scaleY(1.3f).start()
                for(a in imageViews.indices){
                    if(a != i){
                        imageViews[a].animate().setDuration(200).scaleX(1f).scaleY(1f).start()
                    }
                }
            }
        }

        findViewById<ImageView>(R.id.backbutton).setOnClickListener {
            Return_value()
            finish()
        }

        val gunesli_gun_animasyonlari = listOf<LottieAnimationView>(
            binding.gunesli1,
            binding.gunesli2,
            binding.gunesli3,
            binding.gunesli4,

        )

        val gunesli_isim = listOf("gunes.json", "gunesmini.json", "girllaying.json", "gunesmini2.json")
        var gunesli = getDataFromSharedPref(this, "gunesli_shared", "gunesli_data")
        if(gunesli == "empty")
            gunesli = "girllaying.json"
        for (i in gunesli_gun_animasyonlari.indices) {
            val index = i
            gunesli_gun_animasyonlari[i].alpha = if(gunesli_isim[i] == gunesli)1.0f else 0.4f
            gunesli_gun_animasyonlari[i].setOnClickListener {
                saveDataToSharedPRef(this, "gunesli_shared", "gunesli_data", gunesli_isim[index])
                gunesli_gun_animasyonlari[i].animate().alpha(1.0f).setDuration(400).start()
                for(a in gunesli_gun_animasyonlari.indices){
                    if(a != i){
                        gunesli_gun_animasyonlari[a].animate().alpha(0.6f).setDuration(200).start()
                    }
                }
            }
        }


        val yagmurlu_gun_animasyonlari = listOf<LottieAnimationView>(
            binding.yagmur1,
            binding.yagmur2,
            binding.yagmur4,
        )

        val yagmurlu_isim = listOf("rainmini.json", "yagmur.json", "yagmurkadin2.json")
        var yagmurlu = getDataFromSharedPref(this, "yagmurlu_shared", "yagmurlu_data")
        if (yagmurlu == "empty") yagmurlu = "yagmur.json"

        for (i in yagmurlu_gun_animasyonlari.indices) {
            val index = i
            yagmurlu_gun_animasyonlari[i].alpha = if (yagmurlu_isim[i] == yagmurlu) 1.0f else 0.4f

            yagmurlu_gun_animasyonlari[i].setOnClickListener {
                saveDataToSharedPRef(this, "yagmurlu_shared", "yagmurlu_data", yagmurlu_isim[index])
                yagmurlu_gun_animasyonlari[i].animate().alpha(1.0f).setDuration(400).start()
                for (a in yagmurlu_gun_animasyonlari.indices) {
                    if (a != i) {
                        yagmurlu_gun_animasyonlari[a].animate().alpha(0.6f).setDuration(200).start()
                    }
                }
            }
        }

        val bulutlu_gun_animasyonlari = listOf<LottieAnimationView>(
            binding.bulutlu1,
            binding.bulutlu2,
            binding.bulutlu3,
            binding.bulutlu4,
        )

        val bulutlu_isim = listOf("bulutlu.json", "bulutlu2.json", "bulutmini.json", "gokkusagi.json")
        var bulutlu = getDataFromSharedPref(this, "bulutlu_shared", "bulutlu_data")
        if (bulutlu == "empty") bulutlu = "gokkusagi.json"

        for (i in bulutlu_gun_animasyonlari.indices) {
            val index = i
            bulutlu_gun_animasyonlari[i].alpha = if (bulutlu_isim[i] == bulutlu) 1.0f else 0.4f

            bulutlu_gun_animasyonlari[i].setOnClickListener {
                saveDataToSharedPRef(this, "bulutlu_shared", "bulutlu_data", bulutlu_isim[index])
                bulutlu_gun_animasyonlari[i].animate().alpha(1.0f).setDuration(400).start()
                for (a in bulutlu_gun_animasyonlari.indices) {
                    if (a != i) {
                        bulutlu_gun_animasyonlari[a].animate().alpha(0.6f).setDuration(200).start()
                    }
                }
            }
        }



    }

    fun Return_value(){
        val selectedValue = "1"
        val result = Intent()
        result.putExtra("ayar", selectedValue)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onBackPressed() {
        Return_value()
    }

}