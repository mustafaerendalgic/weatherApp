package com.example.weather

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.databinding.ActivityAyarlarBinding
import com.example.weather.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class ayarlar : AppCompatActivity() {

    private lateinit var binding: ActivityAyarlarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAyarlarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"
        binding.switchDarkMode.isChecked = if(darkState == "1") true else false
        if(darkState == "1"){
            binding.gunesli3.visibility = View.GONE
        }

        binding.ukFlag.setOnClickListener{
            val dil = getDataFromSharedPref(this, "dil", "dil").takeIf { it != "empty"}
            if(dil == null || dil != "en") {
                val localeList = LocaleListCompat.forLanguageTags("en")
                AppCompatDelegate.setApplicationLocales(localeList)
                saveDataToSharedPRef(this, "dil", "dil", "en")
            }
        }

        binding.trBayrak.setOnClickListener {
            val dil = getDataFromSharedPref(this, "dil", "dil").takeIf { it != "empty"}
            if(dil == null || dil != "tr"){
                val localeList = LocaleListCompat.forLanguageTags("tr")
                AppCompatDelegate.setApplicationLocales(localeList)
                saveDataToSharedPRef(this, "dil", "dil", "tr")
            }
        }

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
            ContextCompat.getColor(this, R.color.settingsCol1),
            ContextCompat.getColor(this, R.color.settingsCol2),
            ContextCompat.getColor(this, R.color.settingsCol3),
            ContextCompat.getColor(this, R.color.settingsCol4),
            ContextCompat.getColor(this, R.color.settingsCol5),
            ContextCompat.getColor(this, R.color.settingsCol6),
            ContextCompat.getColor(this, R.color.settingsCol7),
            ContextCompat.getColor(this, R.color.settingsCol8),
            ContextCompat.getColor(this, R.color.settingsCol9),
            ContextCompat.getColor(this, R.color.settingsCol10),
            ContextCompat.getColor(this, R.color.settingsCol11),
            ContextCompat.getColor(this, R.color.settingsCol12),
            ContextCompat.getColor(this, R.color.settingsCol13),
            ContextCompat.getColor(this, R.color.settingsCol14),
            ContextCompat.getColor(this, R.color.settingsCol15)
            )



        darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

        if(darkState == "1"){
            Log.e("darkmodeaktif", "patates")
        }
        else
            Log.e("darkmodeapasif", "patates")

        var currentColor = returnTheCurrentColor(this)

        var storedIndex : String

        if(darkState == "1")
            storedIndex = getDataFromSharedPref(this, "dark_index", "dark_index").takeIf { it != "empty" } ?: "2"
        else
            storedIndex = getDataFromSharedPref(this, "light_index", "light_index").takeIf { it != "empty" } ?: "1"

        for (i in imageViews.indices){
            imageViews[i].setOnClickListener {
                val darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

                if(darkState == "1") {
                    saveDataToSharedPRef(this, "dark_index", "dark_index", "$i")
                    saveColorToSharedPrefForDarkMode(this, colorIntToHex(colors[i]))
                }
                else {
                    saveDataToSharedPRef(this, "light_index", "light_index", "$i")
                    saveColorToSharedPref(this, colorIntToHex(colors[i]))
                }

                for (j in imageViews.indices) {
                    val targetScale = if (j == i) 1.3f else 1f
                    imageViews[j]
                        .animate()
                        .setDuration(200)
                        .scaleX(targetScale)
                        .scaleY(targetScale)
                        .start()
                }

            }
        }
        darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

        Log.e("renk şu", "$currentColor")

        for (index in colors.indices) {
            if (index == storedIndex.toInt()) {
                Log.e("renk bulundu", "$currentColor")
                val colorImage = imageViews[index]
                val scale = 1.2f
                colorImage.animate().setDuration(200).scaleX(scale).scaleY(scale).start()
            } else {
                Log.e("renk bulunamadı", "$currentColor")
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
                fonts[i].setTextColor(ContextCompat.getColor(this, R.color.black))
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

        binding.switchDarkMode.setOnCheckedChangeListener { _, ischecked ->
            val value = if(ischecked == true) "1" else "0"
            saveDataToSharedPRef(this, "dark_mode", "dark_mode", value)

            if(value == "1") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }

        findViewById<ImageView>(R.id.backbutton).setOnClickListener {
            Return_value()
            finish()
        }

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

        val gunesli_gun_animasyonlari = listOf<LottieAnimationView>(
            binding.gunesli1,
            binding.gunesli2,
            binding.gunesli4,
            binding.gunesli3,

        )

        darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

        val gunesli_isim = listOf("gunes.json", "gunesmini.json", "gunesmini2.json", "girllaying.json", )
        var gunesli = getGunAnimasyon(this)

        for (i in gunesli_gun_animasyonlari.indices) {
            val index = i
            Log.e("Save", "$gunesli")
            gunesli_gun_animasyonlari[i].alpha = if(gunesli_isim[i] == gunesli)1.0f else 0.4f
            gunesli_gun_animasyonlari[i].setOnClickListener {
                if(darkState == "1") {
                    saveDataToSharedPRef(
                        this,
                        "gunesli_shared_dark",
                        "gunesli_data_dark",
                        gunesli_isim[index]
                    )
                    Log.e("darkSave", "${gunesli_isim[index]}")
                }
                else {
                    saveDataToSharedPRef(
                        this,
                        "gunesli_shared",
                        "gunesli_data",
                        gunesli_isim[index]
                    )
                    Log.e("lightSave", "${gunesli_isim[index]}")
                }

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
        var yagmurlu = getRainAnimation(this)

        for (i in yagmurlu_gun_animasyonlari.indices) {
            val index = i
            yagmurlu_gun_animasyonlari[i].alpha = if (yagmurlu_isim[i] == yagmurlu) 1.0f else 0.4f

            yagmurlu_gun_animasyonlari[i].setOnClickListener {
                if(darkState == "1")
                    saveDataToSharedPRef(this, "yagmurlu_shared_dark", "yagmurlu_data_dark", yagmurlu_isim[index])
                else
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

        var bulutlu = getCloudyAnimation(this)


        for (i in bulutlu_gun_animasyonlari.indices) {
            val index = i
            bulutlu_gun_animasyonlari[i].alpha = if (bulutlu_isim[i] == bulutlu) 1.0f else 0.4f

            bulutlu_gun_animasyonlari[i].setOnClickListener {

                if(darkState == "1")
                    saveDataToSharedPRef(this, "bulutlu_shared_dark", "bulutlu_data_dark", bulutlu_isim[index])
                else
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