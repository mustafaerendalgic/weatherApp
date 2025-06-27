package com.example.weather

import android.Manifest
import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.AppLocalesStorageHelper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.weather.UI.AdapterForecast
import com.example.weather.UI.AdapterHourly
import com.example.weather.UI.AdapterHourlyDetailed
import com.example.weather.data.DailyForecast
import com.example.weather.data.DetayChartInfo
import com.example.weather.data.ForecastChart
import com.example.weather.data.GunlukDataSet
import com.example.weather.data.GunlukTempChartEntry
import com.example.weather.data.HaftalikDataSet
import com.example.weather.data.HaftalikEntry
import com.example.weather.data.HourlyList
import com.example.weather.data.WeatherResponse
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.retrofit.RetrApi
import com.example.weather.util.getLocFromSharedPref
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import com.example.weather.util.*
import com.example.weather.viewmodel.WeatherViewModel
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var check = 0

    lateinit var  binding : ActivityMainBinding

    private val viewModel : WeatherViewModel by viewModels()

    private var device_latitude : Double? = null
    private var device_longitude : Double? = null
    private var isRvAttached = false

    private var Adapter_hourly1 = AdapterHourly("#7c6fde", this, "annie")
    private var Adapter_hourly_detailed = AdapterHourlyDetailed("#7c6fde", this, "annie")

    private var AdapterForecast = AdapterForecast("#7c6fde", this, "annie")
    private var is_forecast_adapter_att = false

    private var yagmur_animasyon = "yagmurkadin2.json"

    private var bulutlu_animasyon = "gokkusagi.json"
    private var gece_animasyon = "gecemoon.json"
    private var kar_animasyon = "karbuyuk.json"


    private var gun_animasyon =  "girllaying.json"

    private var currentState = gun_animasyon
    private var animationCheck = false
    private var playitonce = false
    private var switch_Detay = "0"
    private var switch_Detay_previous = "2"

    private var pickedFont = "annie"
    private var previousFont = "annie"

    private var internetAlertCheck = false

    private var databaseFlag = false

    private var uiFlag = false

    private var anFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeTheLanguage(this)

        var darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

        gun_animasyon =  if(darkState == "1") "gunesmini2.json" else "girllaying.json"
        if(darkState == "1")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        checkLocPerm()

        val chart = binding.saatlikChart
        val chartForecast = binding.forecastLineChart
        val grafikSwitch = binding.grafikSwitch
        val grafikswitchForecast = binding.grafikSwitchForecast
        var switch_state = getDataFromSharedPref(this, "switch_state", "switch_state")
        if(switch_state == "empty"){
            switch_state = "false"
        }
        var forecast_switch_state = getDataFromSharedPref(this, "forecast_switch_state", "switch_state")
        if(forecast_switch_state == "empty")
            forecast_switch_state = "true"
        val rv = binding.saatlik2
        val rv_forecast = binding.forecast

        binding.grafikSwitchForecast.setOnClickListener {
            if (binding.grafikSwitchForecast.isChecked == false) {
                binding.forecastLineChart.animate().setDuration(1000)
                    .translationX(-resources.displayMetrics.widthPixels.toFloat())
                    .setInterpolator(AnticipateOvershootInterpolator()).withEndAction {
                        binding.forecast.translationX = resources.displayMetrics.widthPixels.toFloat()
                        binding.forecast.visibility = View.VISIBLE
                        binding.forecast.animate().translationX(0f).setDuration(1000)
                            .setInterpolator(AnticipateOvershootInterpolator()).start()
                        binding.forecastLineChart.visibility = View.GONE
                    }.start()

                saveDataToSharedPRef(this, "forecast_switch_state", "switch_state", "false")
            } else {
                binding.forecast.animate().setDuration(1000)
                    .translationX(-resources.displayMetrics.widthPixels.toFloat())
                    .setInterpolator(AnticipateOvershootInterpolator()).withEndAction {
                        binding.forecast.visibility = View.GONE
                        binding.forecastLineChart.translationX = resources.displayMetrics.widthPixels.toFloat()
                        binding.forecastLineChart.visibility = View.VISIBLE
                        binding.forecastLineChart.animate().translationX(0f).setDuration(1000)
                            .setInterpolator(AnticipateOvershootInterpolator()).start()
                    }.start()

                saveDataToSharedPRef(this, "forecast_switch_state", "switch_state", "true")
            }
        }

        grafikSwitch.setOnClickListener {

            if(grafikSwitch.isChecked == false) {
                chart.animate().setDuration(1000).translationX(-resources.displayMetrics.widthPixels.toFloat()).setInterpolator(
                    AnticipateOvershootInterpolator()).withEndAction {
                    rv.translationX = resources.displayMetrics.widthPixels.toFloat()
                    rv.visibility = View.VISIBLE
                    rv.animate().translationX(0f).setDuration(1000).setInterpolator(
                        AnticipateOvershootInterpolator()).start()
                    chart.visibility = View.GONE
                }.start()

                saveDataToSharedPRef(this, "switch_state", "switch_state", "false")
            }
            else{
                rv.animate().setDuration(1000).translationX(-resources.displayMetrics.widthPixels.toFloat()).setInterpolator(
                    AnticipateOvershootInterpolator()).withEndAction {
                    rv.visibility = View.GONE
                    chart.translationX = resources.displayMetrics.widthPixels.toFloat()
                    chart.visibility = View.VISIBLE
                    chart.animate().translationX(0f).setDuration(1000).setInterpolator(
                        AnticipateOvershootInterpolator()).withEndAction {

                    }.start()

                }.start()
                saveDataToSharedPRef(this, "switch_state", "switch_state", "true")
            }

        }

        if(switch_state == "false") {
            grafikSwitch.isChecked = false
            chart.visibility = View.GONE
            rv.visibility = View.VISIBLE

        }
        else{
            grafikSwitch.isChecked = true
            chart.visibility = View.VISIBLE
            rv.visibility = View.GONE

        }

        if(forecast_switch_state == "false") {
            grafikswitchForecast.isChecked = false
            chartForecast.visibility = View.GONE
            rv_forecast.visibility = View.VISIBLE

        }
        else{
            grafikswitchForecast.isChecked = true
            chartForecast.visibility = View.VISIBLE
            rv_forecast.visibility = View.GONE

        }

        val settings = binding.ayarlar
        settings.setOnClickListener {
            val settingsIntent = Intent(this, ayarlar::class.java)
            startActivityForResult(settingsIntent, 1001)
        }

        val refresh = binding.swipe
        refresh.setOnRefreshListener {
            uiFlag = false
            if(checkConnection(this)) {
                getUserLoc(object : LocationCallback {
                    override fun onLocationReceived(latitude: Double, longitude: Double) {
                        fetchWeatherData(latitude, longitude)
                    }
                })
            }
            else {
                if(internetAlertCheck == false) {
                    customAlertDisplay(this)
                    refresh.isRefreshing = false
                    internetAlertCheck = true
                }
            }

        }

        pickedFont = getDataFromSharedPref(this, "selected_font", "selected_font")
        if(pickedFont != previousFont){
            Adapter_hourly1.fontUpdate(pickedFont)
            Adapter_hourly_detailed.fontUpdate(pickedFont)
            AdapterForecast.fontUpdate(pickedFont)
            previousFont = pickedFont
        }

        val loc = getLocFromSharedPref(this)
        loc?.let{
            lifecycleScope.launch {
                fetchWeatherData(it.first, it.second)
            }
        }


    }

    fun updateUI(weatherData : WeatherResponse): Int {

        val list = convertHourlyToList(weatherData) // olduğumuz saatten başlar
        val list2 = convertHourlyToList(weatherData, 1)

        if (device_latitude == null || device_longitude == null) {
            Log.d("locationeren", "Location is null, skipping updateUI")

        }

        else if (!uiFlag){
            uiFlag = true
            Log.e("udate ui", "ui update: ${weatherData}")

            if (!checkConnection(this) && databaseFlag == false) {
                databaseFlag = true
                lifecycleScope.launch {
                    val cached = viewModel.getWeather()
                    if (cached != null) {
                        updateUI(cached)
                    }

                }

            } else {
                getUserLoc(object : LocationCallback {
                    override fun onLocationReceived(latitude: Double, longitude: Double) {
                        if (!databaseFlag)
                            fetchWeatherData(latitude, longitude)
                    }
                })

            }
            var darkState = getDataFromSharedPref(this, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"
            if (!animationCheck) {
                gun_animasyon = getGunAnimasyon(this)
                yagmur_animasyon = getRainAnimation(this)
                bulutlu_animasyon = getCloudyAnimation(this)
                animationCheck = true
            }

            binding.derece.text = "${weatherData.current.temperature_2m.toInt()}°"
            binding.hissedilen.text = ContextCompat.getString(this, R.string.hisSicaklik) +
                ": ${weatherData.current.apparent_temperature.toInt()}°"
            binding.nem.text =
                "${weatherData.current.relative_humidity_2m}${weatherData.current_units.relative_humidity_2m}"
            binding.ruzgarHizi.text =
                "${weatherData.current.wind_speed_10m} ${weatherData.current_units.wind_speed_10m}"

            binding.ruzgarYonu.text = "${getWindDirectionSimple(this, weatherData.current.wind_direction_10m)}"

            binding.bulutluluk.text =
                "${weatherData.current.cloud_cover}${weatherData.current_units.cloud_cover}"
            binding.saganak.text =
                "${weatherData.current.showers} ${weatherData.current_units.showers}"
            binding.yagmur.text = "${weatherData.current.rain} ${weatherData.current_units.rain}"
            binding.kar.text =
                "${weatherData.current.snowfall} ${weatherData.current_units.snowfall}"
            binding.basinc.text =
                "${weatherData.current.surface_pressure} ${weatherData.current_units.surface_pressure}"
            binding.uv.text = "${weatherData.hourly.uv_index[1]} (${getUvRiskLevel(weatherData.hourly.uv_index[1].toDouble(), this)})"

            val previous_state = currentState
            gun_animasyon =  getGunAnimasyon(this)
            currentState = updateState(weatherData)

            val mainAnimation = binding.gun

            if (currentState != previous_state) {

                var mainAnimationName = currentState
                mainAnimation.setAnimation(mainAnimationName)
                mainAnimation.playAnimation()

            }

            if (currentState == "gunes.json")
                mainAnimation.scaleType = ImageView.ScaleType.FIT_CENTER
            else
                mainAnimation.scaleType = ImageView.ScaleType.CENTER_CROP

            if (!playitonce) {
                mainAnimation.setAnimation(currentState)
                mainAnimation.playAnimation()
                playitonce = true
            }

            if (check == 0) {
                val maxminpair = findMaxMin(list2)
                findViewById<TextView>(R.id.maxmin).text =
                    "Max : ${maxminpair.first}°C / Min : ${maxminpair.second}°C"
                check = 1
            }


        }

        val hasSeenIntro = getDataFromSharedPref(this, "intro", "intro")

        if(hasSeenIntro != "true"){
            startTour()
        }

        updateRVs(weatherData)

        lifecycleScope.launch(Dispatchers.IO) {

            val entries = list.mapIndexedNotNull { int, list1 ->
                Entry(int.toFloat(), list1.temperature_2m.toFloat())
            }

            val entriesApp = list.mapIndexedNotNull { int, list1 ->
                Entry(int.toFloat(), list1.apparent_temperature.toFloat())
            }

            val entriesHum = list.mapIndexedNotNull { int, list1 ->
                Entry(int.toFloat(), list1.relative_humidity_2m.toFloat())
            }

            val entriesWind = list.mapIndexedNotNull { int, list1 ->
                BarEntry(int.toFloat(), list1.wind_speed_10m.toFloat())
            }


            val valueformatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String? {
                    return if (entry?.x == 0f) "" else "${entry?.y?.toInt()}°"
                }
            }

            val valueformatterHum = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String? {
                    return if (entry?.x == 0f) "" else "${entry?.y?.toInt()}%"
                }
            }

            val alpha = 80

            val gunlukTempChartEntry = GunlukTempChartEntry(entries, entriesApp)
            val detayChartInfo = DetayChartInfo(entriesWind, entriesHum)
            val dataSets = GunlukDataSet(
                LineDataSet(entries, ContextCompat.getString(this@MainActivity, R.string.temperature) + " (°C)").apply {
                fillAlpha = alpha
                color =
                    Color.parseColor(returnTheCurrentColor(this@MainActivity))
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircles(false)
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                valueFormatter = valueformatter
                valueTextSize = 12f
                },
                LineDataSet(entriesApp, ContextCompat.getString(this@MainActivity, R.string.feels_like) + " (°C)").apply {
                    setDrawValues(false)
                    fillAlpha = alpha
                    color = ContextCompat.getColor(this@MainActivity, R.color.barAndHumdColors)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawCircles(false)
                    valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    valueFormatter = valueformatter
                    valueTextSize = 12f
                },
                LineDataSet(entriesHum, ContextCompat.getString(this@MainActivity, R.string.humidity) + " (%)").apply {
                    fillAlpha = alpha
                    valueTextSize = 12f
                    color = ContextCompat.getColor(this@MainActivity, R.color.barAndHumdColors)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawCircles(false)
                    valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    valueFormatter = valueformatterHum
                    isHighlightEnabled = false
                },
                BarDataSet(entriesWind,  ContextCompat.getString(this@MainActivity, R.string.ruzgar_hizi) + " (km/h)").apply {
                    valueTextSize = 12f
                    color = ContextCompat.getColor(this@MainActivity, R.color.barAndHumdColors)
                    valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    isHighlightEnabled = false
                }
                )

            val listForForecastChart = convertHourlyToListForecastChart(weatherData, this@MainActivity)

            val rainIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.rain)
            val sunIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.sun_74)
            val offset = 0.1f

            val entriesMax = listForForecastChart.mapIndexedNotNull { int, item ->
                val entry = Entry(int.toFloat() + offset, item.max_temp.toFloat())
                entry
            }

            val entriesSun = listForForecastChart.mapIndexedNotNull { int, item ->
                val entry = Entry(
                    int.toFloat() + offset,
                    (item.max_temp.toFloat() + item.min_temp.toFloat()) / 2f
                )
                entry.icon = if (item.yagmur == 1) rainIcon else sunIcon
                entry
            }

            val entriesMin = listForForecastChart.mapIndexedNotNull { int, item ->
                Entry(
                    int.toFloat() + offset,
                    item.min_temp.toFloat()
                )
            }

            val valueFormatter1 = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String? {
                    return if (entry?.x == 0f) "" else "${entry?.y?.toInt()}°"
                }
            }

            val dataMax = LineDataSet(entriesMax, ContextCompat.getString(this@MainActivity, R.string.maxSicaklik) + " (°C)").apply {
                valueTextSize = 16f
                isHighlightEnabled = false
                setDrawCircles(true)
                setCircleColors(
                    Color.parseColor(
                        returnTheCurrentColor(this@MainActivity)
                    )
                )
                lineWidth = 2f
                circleSize = 4f
                valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                color =
                    Color.parseColor(returnTheCurrentColor(this@MainActivity))
                valueFormatter = valueFormatter1
            }

            val dataMin = LineDataSet(entriesMin, ContextCompat.getString(this@MainActivity, R.string.minSicaklik) + " (°C)").apply {
                valueTextSize = 16f
                isHighlightEnabled = false
                setDrawCircles(true)
                setCircleColors(Color.parseColor("#aa000000"))
                lineWidth = 2f
                circleSize = 4f
                valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                color = ContextCompat.getColor(this@MainActivity, R.color.subSiyahText)
                valueFormatter = valueFormatter1
            }

            val dataSun = LineDataSet(entriesSun, "").apply {
                color = ContextCompat.getColor(this@MainActivity, R.color.arkaplanGenel)
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(false)
                setDrawIcons(true)
            }

            val haftalikDataSet = HaftalikDataSet(dataMax, dataMin, dataSun)
            val haftalikEntry = HaftalikEntry(entriesMax, entriesMin, entriesSun)

            withContext(Dispatchers.Main) {
                updateCharts(weatherData, list, gunlukTempChartEntry, detayChartInfo, dataSets, haftalikDataSet, haftalikEntry)
            }
        }

        return 0

    }

    fun updateRVs(weatherData: WeatherResponse){
        val list = convertHourlyToList(weatherData)
        val switch_value = getDataFromSharedPref(this, "switch_detay", "switch_detay")
        switch_Detay = if (switch_value == "1" || switch_value == "0") switch_value else "0"

        val rv = binding.saatlik2

        if (rv != null) {
            if (!isRvAttached || switch_Detay_previous != switch_Detay) {
                switch_Detay_previous = switch_Detay
                if (switch_Detay == "0") {
                    RV_set_up_hourly(Adapter_hourly1, list)
                    val tema_renk = returnTheCurrentColor(this)
                    if (tema_renk != null) {
                        Adapter_hourly1.backGUpdate(tema_renk)
                    }
                } else {
                    RV_set_up_hourly_detailed(Adapter_hourly_detailed, list)
                    val tema_renk = returnTheCurrentColor(this)
                    if (tema_renk != null) {
                        Adapter_hourly_detailed.backGUpdate(tema_renk)
                    }
                }
                isRvAttached = true
                switch_Detay_previous = switch_Detay
            }
        }


        if (!is_forecast_adapter_att) {
            val rv = binding.forecast
            val list = convertHourlyToListForecast(weatherData, this)
            AdapterForecast.submitList(list)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = AdapterForecast
            rv.layoutManager = layoutManager
            returnTheCurrentColor(this)?.let { AdapterForecast.backGUpdate(it) }
            is_forecast_adapter_att = true
        }

        val currentColorForCards = returnTheCurrentColor(this@MainActivity)

        pickedFont = getDataFromSharedPref(this, "selected_font", "selected_font")
        if(pickedFont != previousFont){
            Adapter_hourly1.fontUpdate(pickedFont)
            Adapter_hourly_detailed.fontUpdate(pickedFont)
            AdapterForecast.fontUpdate(pickedFont)
            previousFont = pickedFont
        }

        Adapter_hourly1.backGUpdate(currentColorForCards)
        Adapter_hourly_detailed.backGUpdate(currentColorForCards)
        AdapterForecast.backGUpdate(currentColorForCards)

        Adapter_hourly1.notifyDataSetChanged()
        Adapter_hourly_detailed.notifyDataSetChanged()
        AdapterForecast.notifyDataSetChanged()

    }

    fun updateCharts(weatherData: WeatherResponse, list : List<HourlyList>, gunlukInfo: GunlukTempChartEntry, detay: DetayChartInfo, dataSetGunluk: GunlukDataSet, haftalikDataSet: HaftalikDataSet, haftalikEntry: HaftalikEntry){

        var chart = binding.saatlikChartLine
        var chartBar = binding.saatlikChartBar
        var chartHum = binding.saatlikHumChartLine

        if (chart != null) {

            val entries = gunlukInfo.Temp
            val entriesApp = gunlukInfo.TempApp
            val entriesWind = detay.ruzgarBar
            val entriesHum = detay.nemEntry

            val dataSet = dataSetGunluk.dataSet
            val ruzHizDataSet = dataSetGunluk.windDataSet
            val nemDataSet = dataSetGunluk.humDataSet
            val appDataSet = dataSetGunluk.appDataSet

            val combinedLineData = LineData()

            val combinedBarData = BarData()

            val detay = getDataFromSharedPref(this, "switch_detay", "switch_detay")

            val saatEtiketleri = list.map { it.time.substringAfter("T") }

            if (detay == "1") {

                chartBar.visibility = View.VISIBLE
                chartHum.visibility = View.VISIBLE

                combinedBarData.barWidth = 0.4f

                chartBar.xAxis.apply {
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    axisLineColor = Color.parseColor(returnTheCurrentColor(this@MainActivity))
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    //granularity = 3f
                    isGranularityEnabled = true
                    valueFormatter =
                        IndexAxisValueFormatter(saatEtiketleri)
                }

                chartBar.axisLeft.apply {
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    setDrawAxisLine(true)
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    //granularity = 2f
                }

                chartBar.axisRight.setDrawGridLines(false)
                chartBar.axisLeft.setDrawGridLines(false)
                chartBar.xAxis.setDrawGridLines(false)

                chartHum.xAxis.apply {
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    axisLineColor = Color.parseColor(returnTheCurrentColor(this@MainActivity))
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    //granularity = 2f
                    isGranularityEnabled = true
                    valueFormatter =
                        IndexAxisValueFormatter(saatEtiketleri)
                }

                chartHum.axisLeft.apply {
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                    setDrawAxisLine(true)
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    granularity = 2f
                }

                chartHum.axisRight.setDrawGridLines(false)
                chartHum.axisLeft.setDrawGridLines(false)
                chartHum.xAxis.setDrawGridLines(false)

                chartHum.axisRight.setDrawAxisLine(false)
                chartBar.axisRight.setDrawAxisLine(false)
                chartHum.axisRight.setDrawLabels(false)
                chartBar.axisRight.setDrawLabels(false)

                chart.axisLeft.axisMaximum = maxOf(entries.maxOf { it.y }, entriesApp.maxOf { it.y }) + 6f
                chartHum.axisLeft.axisMaximum = entriesHum.maxOf { it.y } + 10f
                chartBar.axisLeft.axisMaximum = entriesWind.maxOf { it.y } + 4f

                chartHum.description.text = ""
                chartBar.description.text = ""

                val humData = LineData(nemDataSet)

                chartHum.data = humData

                combinedBarData.addDataSet(ruzHizDataSet)

                chartBar.data = combinedBarData

                chartBar.setDragEnabled(true)
                chartBar.setScaleEnabled(false)
                chartBar.setVisibleXRangeMaximum(8f)
                chartBar.moveViewToX(-1f)

                chartHum.setDragEnabled(true)
                chartHum.setScaleEnabled(false)
                chartHum.setVisibleXRangeMaximum(8f)
                chartHum.moveViewToX(0f)



                chartBar.data.notifyDataChanged()
                chartBar.notifyDataSetChanged()
                chartHum.notifyDataSetChanged()


            } else {
                chartBar.visibility = View.GONE
                chartHum.visibility = View.GONE
            }

            chart.xAxis.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                setDrawGridLines(false)
                axisLineColor = Color.parseColor(returnTheCurrentColor(this@MainActivity))
                axisLineWidth = 2f
                textSize = 10f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                //granularity = 3f
                isGranularityEnabled = true
                valueFormatter =
                    IndexAxisValueFormatter(saatEtiketleri)
            }

            chart.description.isEnabled = false

            chart.axisLeft.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                axisLineWidth = 2f
                textSize = 10f
                setDrawGridLines(false)
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                granularity = 3f

            }

            chart.legend.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartHum.legend.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartBar.legend.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chart.xAxis.setAvoidFirstLastClipping(true)

            combinedLineData.addDataSet(dataSet)
            combinedLineData.addDataSet(appDataSet)

            chart.data = combinedLineData

            chart.setDragEnabled(true)
            chart.setScaleEnabled(false)
            chart.setVisibleXRangeMaximum(8f)
            chart.moveViewToX(0f)


            chartHum.setDragEnabled(true)
            chartHum.setScaleEnabled(false)
            chartHum.setVisibleXRangeMaximum(8f)
            chartHum.moveViewToX(0f)

            chart.axisRight.isEnabled = false

            chart.notifyDataSetChanged()

            chart.invalidate()
            chartBar.invalidate()
            chartHum.invalidate()

        }

        val chartForec = binding.forecastLineChart

        if (chartForec != null) {

            val days = convertHourlyToListForecastChart(weatherData, this@MainActivity).map { it.time.substringAfter(",").trim() }

            val entriesMax = haftalikEntry.tempMax
            val dataMin = haftalikDataSet.tempMin
            val dataMax = haftalikDataSet.tempMax
            val dataSun = haftalikDataSet.dataSun

            chartForec.description.text = ""
            chartForec.legend.apply {
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartForec.xAxis.apply {
                textColor = ContextCompat.getColor(this@MainActivity, R.color.textSiyah)
                valueFormatter = IndexAxisValueFormatter(days)
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                setDrawGridLines(false)
                textSize = 12f
            }

            chartForec.setExtraOffsets(20f, 0f, 0f, 10f)
            chartForec.isHighlightPerTapEnabled = false

            chartForec.axisLeft.apply {
                isEnabled = false
            }

            chartForec.extraTopOffset = 10f
            chartForec.setScaleEnabled(false)
            chartForec.setDragEnabled(true)
            chartForec.setVisibleXRangeMaximum(3f)
            chartForec.moveViewToX(-2f)
            chartForec.axisLeft.setDrawGridLines(false)
            chartForec.xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
            chartForec.axisRight.apply {
                isEnabled = false
            }


            val dataTemp = LineData()
            dataTemp.addDataSet(dataMax)
            dataTemp.addDataSet(dataMin)
            dataTemp.addDataSet(dataSun)

            chartForec.data = dataTemp

            chartForec.notifyDataSetChanged()
            chartForec.invalidate()

        }
    }

    fun updateState(weatherData: WeatherResponse) : String{

        val time = getLocalTime().split(":")[0].toDouble()

        return if(weatherData.current.rain > 0 || weatherData.current.showers > 0){
            yagmur_animasyon
        }
        else if(weatherData.current.snowfall > 0){
            kar_animasyon
        }
        else if (time >= 19 || time < 7){
            gece_animasyon
        }
        else if (weatherData.current.cloud_cover > 90){
            bulutlu_animasyon
        }
        else{
            gun_animasyon
        }

    }

    private val locReqCode = 1001
    private fun checkLocPerm(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locReqCode)
        }
        else{
            getUserLoc(object : LocationCallback {
                override fun onLocationReceived(latitude: Double, longitude: Double) {
                    uiFlag = false
                    fetchWeatherData(latitude, longitude)

                }
            })
        }
    }

    interface LocationCallback{
        fun onLocationReceived(latitude : Double, longitude : Double)
    }

    private fun getUserLoc(callback : LocationCallback){

        try {

            val shPair = getLocFromSharedPref(this)

            shPair?.let {
                callback.onLocationReceived(shPair.first, shPair.second)
                binding.districtName.text = "${getAreaName(shPair.first, shPair.second, this).get(0)}, ${getAreaName(shPair.first, shPair.second, this).get(1)}"
                device_latitude = it.first
                device_longitude = it.second
            }

            val fusedLocClient = LocationServices.getFusedLocationProviderClient(this)

            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    val location = p0.lastLocation
                    location?.let {
                        callback.onLocationReceived(location.latitude, location.longitude)
                        saveLocToSharedPref(
                            this@MainActivity,
                            location.latitude,
                            location.longitude
                        )
                        device_latitude = location.latitude
                        device_longitude = location.longitude
                        binding.districtName.text = "${
                            getAreaName(
                                location.latitude,
                                location.longitude,
                                this@MainActivity
                            ).get(0)
                        }, ${
                            getAreaName(
                                location.latitude,
                                location.longitude,
                                this@MainActivity
                            ).get(1)
                        }"
                        fusedLocClient.removeLocationUpdates(this)
                        Log.d(
                            "locationeren",
                            "Latitude: ${device_latitude}, Longitude $device_longitude"
                        )
                    }
                }
            }
            fusedLocClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun fetchWeatherData(latitude : Double, longitude: Double) {
        val api = RetrApi.RetrofitBuilder
        api.getWeather(latitude, longitude)
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        weatherData?.let {
                            updateUI(weatherData)
                            Log.e("udate ui", "weather data tanım: ${weatherData}")
                            viewModel.setWeatherToRoom(weatherData)
                        }
                    }
                    binding.swipe.isRefreshing = false
                }
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch weather: ${t.message}")

                    if (!internetAlertCheck) {
                        customAlertDisplay(this@MainActivity)
                        internetAlertCheck = true
                    }

                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            Log.d("OFFLINE", "Coroutine started")
                            val cached = withContext(Dispatchers.IO) {
                                try {
                                    viewModel.getWeather()
                                } catch (e: Exception) {
                                    Log.e("DB_ERROR", "Error fetching cached weather: ${e.message}")
                                    null
                                }
                            }

                            if (cached != null) {
                                Log.d("OFFLINE", "Cached data fetched: $cached")
                                updateUI(cached)
                            } else {
                                Log.e("OFFLINE", "No cached weather data found.")
                            }
                        } catch (e: Exception) {
                            Log.e("COROUTINE_ERROR", "Unhandled exception in lifecycleScope: ${e.message}", e)
                        } finally {
                            binding.swipe.isRefreshing = false
                        }
                    }

                }

            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == locReqCode){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getUserLoc(object : LocationCallback {
                    override fun onLocationReceived(latitude: Double, longitude: Double) {
                        fetchWeatherData(latitude, longitude)
                    }
                })
            }
        }
    }

    fun RV_set_up_hourly(adapter : AdapterHourly, list : List<HourlyList>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter.submitList(list)
        val rv = binding.saatlik2
        rv.adapter = null
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }

    fun RV_set_up_hourly_detailed(adapter : AdapterHourlyDetailed, list : List<HourlyList>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val rv = binding.saatlik2
        rv.adapter = null
        adapter.submitList(list)
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val mainAnimation = binding.gun

            var selected_font = getDataFromSharedPref(this, "selected_font", "selected_font")
            if(selected_font == "empty"){
                selected_font = "annie"
            }

            val ayar = data?.getStringExtra("ayar")

            ayar?.let{

                uiFlag = false

                binding.gun.visibility = View.VISIBLE

                var selected_gunesli = getGunAnimasyon(this)
                var selected_bulutlu = getCloudyAnimation(this)
                var selected_yagmurlu = getRainAnimation(this)

                if(currentState == gun_animasyon && anFlag != true) {
                    Log.e("kontrol2", "selgun $selected_gunesli")
                    val factor = 3f
                    val polator = OvershootInterpolator(factor)
                    Log.e("kontrol2", "curr $currentState")
                    if (currentState != selected_gunesli) {
                        gun_animasyon = selected_gunesli
                        Log.e("kontrol3", "gunan $gun_animasyon")
                        currentState = gun_animasyon
                        Log.e("kontrol4", "gunan $currentState")
                        mainAnimation.animate().translationX(-mainAnimation.width.toFloat())
                            .setDuration(600).setInterpolator(polator).withEndAction {
                                mainAnimation.setAnimation(selected_gunesli)
                                if (selected_gunesli == "gunes.json")
                                    mainAnimation.scaleType = ImageView.ScaleType.FIT_CENTER
                                else
                                    mainAnimation.scaleType = ImageView.ScaleType.CENTER_CROP
                                animationCheck = false
                                mainAnimation.translationX = mainAnimation.width.toFloat()
                                mainAnimation.playAnimation()
                                mainAnimation.animate().translationX(0f).setDuration(600).setInterpolator(polator).start()
                            }.start()
                    }
                }


                if(yagmur_animasyon != selected_yagmurlu && currentState == yagmur_animasyon){
                    saveDataToSharedPRef(this, "yagmurlu_shared", "yagmurlu_data", selected_yagmurlu)
                    val factor = 3f
                    val polator = OvershootInterpolator(factor)
                    if(currentState == yagmur_animasyon) {
                        yagmur_animasyon = selected_yagmurlu
                        currentState = selected_yagmurlu
                        mainAnimation.animate().translationX(-mainAnimation.width.toFloat())
                            .setDuration(600).setInterpolator(polator).withEndAction {
                                mainAnimation.setAnimation(selected_yagmurlu)
                                animationCheck = false
                                mainAnimation.translationX = mainAnimation.width.toFloat()
                                mainAnimation.playAnimation()
                                mainAnimation.animate().translationX(0f).setDuration(600).setInterpolator(polator).start()
                            }.start()
                        mainAnimation.setAnimation(it)
                        mainAnimation.playAnimation()
                    }
                }

                if(bulutlu_animasyon != selected_bulutlu && currentState == bulutlu_animasyon){
                    saveDataToSharedPRef(this, "bulutlu_shared", "bulutlu_data", selected_bulutlu)
                    val factor = 3f
                    val polator = OvershootInterpolator(factor)
                    if(currentState == bulutlu_animasyon) {
                        bulutlu_animasyon = selected_bulutlu
                        currentState = selected_bulutlu
                        animationCheck = false
                        mainAnimation.animate().translationX(-mainAnimation.width.toFloat())
                            .setDuration(600).setInterpolator(polator).withEndAction {
                                mainAnimation.setAnimation(selected_bulutlu)
                                animationCheck = false
                                mainAnimation.translationX = mainAnimation.width.toFloat()
                                mainAnimation.playAnimation()
                                mainAnimation.animate().translationX(0f).setDuration(600).setInterpolator(polator).start()
                            }.start()
                        mainAnimation.setAnimation(it)
                        mainAnimation.playAnimation()
                    }
                }

                pickedFont = getDataFromSharedPref(this, "selected_font", "selected_font")
                if(pickedFont != previousFont){
                    Adapter_hourly1.fontUpdate(pickedFont)
                    Adapter_hourly_detailed.fontUpdate(pickedFont)
                    AdapterForecast.fontUpdate(pickedFont)
                    previousFont = pickedFont
                }

                val weatherResponse = viewModel.weatherResponse
                if(weatherResponse != null){
                    updateUI(weatherResponse)
                }
                else{
                    val loc = getLocFromSharedPref(this)
                    if(loc != null){
                        fetchWeatherData(loc.first, loc.second)
                    }
                }

            }

        }

    }

    fun customAlertDisplay(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)
        val alert = AlertDialog.Builder(context).setView(view).create()
        view.findViewById<TextView>(R.id.tamam).setOnClickListener {
            internetAlertCheck = true
            alert.dismiss()
        }
        alert.show()
    }


    fun startTour(){
        binding.root.post {

            TapTargetSequence(this).targets(

                TapTarget.forView(binding.ayarlar, "Ayarlar",ContextCompat.getString(this, R.string.ttAyarlar))
                    .outerCircleColor(R.color.outerCircle)
                    .targetCircleColor(R.color.innerCircle)
                    .cancelable(true)
                    .descriptionTextColor(R.color.white)
                    .textColor(R.color.white)
                    .transparentTarget(true)
                    .targetRadius(60)
                    .id(1)

            ).listener(object: TapTargetSequence.Listener{
                override fun onSequenceFinish() {
                    saveDataToSharedPRef(this@MainActivity, "intro", "intro", "true")
                }

                override fun onSequenceStep(
                    lastTarget: TapTarget?,
                    targetClicked: Boolean
                ) {
                    when(lastTarget?.id()){
                        1->{
                            binding.scrollView.post {
                                binding.scrollView.smoothScrollTo(0, binding.grafikSwitch.top)

                                binding.grafikSwitch.postDelayed({
                                    TapTargetSequence(this@MainActivity).targets(

                                        TapTarget.forView(binding.grafikSwitch, "Grafik Seçeneği",ContextCompat.getString(this@MainActivity, R.string.ttgrafikSwitch))
                                            .outerCircleColor(R.color.outerCircle)
                                            .targetCircleColor(R.color.innerCircle)
                                            .cancelable(true)
                                            .descriptionTextColor(R.color.white)
                                            .textColor(R.color.white)
                                            .transparentTarget(true)
                                            .targetRadius(60),

                                        TapTarget.forView(binding.grafikSwitchForecast, "Grafik Seçeneği",ContextCompat.getString(this@MainActivity, R.string.ttgrafikSwitch))
                                            .outerCircleColor(R.color.outerCircle)
                                            .targetCircleColor(R.color.innerCircle)
                                            .cancelable(true)
                                            .descriptionTextColor(R.color.white)
                                            .textColor(R.color.white)
                                            .transparentTarget(true)
                                            .targetRadius(60)
                                    ).listener(object: TapTargetSequence.Listener{
                                        override fun onSequenceFinish() {
                                            saveDataToSharedPRef(this@MainActivity, "intro", "intro", "true")
                                        }

                                        override fun onSequenceStep(
                                            lastTarget: TapTarget?,
                                            targetClicked: Boolean
                                        ) {

                                        }

                                        override fun onSequenceCanceled(lastTarget: TapTarget?) {

                                        }

                                    }).start()
                                }, 500)
                            }
                        }
                    }
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {

                }

            }).start()

            saveDataToSharedPRef(this, "intro", "intro", "true")

        }
    }
}