package com.example.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.weather.UI.AdapterForecast
import com.example.weather.UI.AdapterHourly
import com.example.weather.UI.AdapterHourlyDetailed
import com.example.weather.data.DailyForecast
import com.example.weather.data.ForecastChart
import com.example.weather.data.HourlyList
import com.example.weather.data.WeatherResponse
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



class MainActivity : AppCompatActivity() {
    private var check = 0


    private val viewModel : WeatherViewModel by viewModels()

    private var device_latitude : Double? = null
    private var device_longitude : Double? = null
    private var isRvAttached = false
    private var Adapter_hourly1 = AdapterHourly("#7c6fde", this, "annie")
    private var Adapter_hourly_detailed = AdapterHourlyDetailed("#7c6fde", this, "annie")

    private var AdapterForecast = AdapterForecast("#7c6fde", this, "annie")
    private var is_forecast_adapter_att = false

    private var yagmur_animasyon = "yagmurkadin2.json"

    private var gun_animasyon = "girllaying.json"
    private var bulutlu_animasyon = "gokkusagi.json"
    private var gece_animasyon = "gecemoon.json"
    private var kar_animasyon = "karbuyuk.json"

    private var currentState = gun_animasyon
    private var animationCheck = false
    private var playitonce = false
    private var switch_Detay = "0"
    private var switch_Detay_previous = "2"

    private var pickedFont = "annie"
    private var previousFont = "annie"

    private var internetAlertCheck = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        checkLocPerm()

        Glide.with(this).asGif().load(R.drawable.nem).into(findViewById<ImageView>(R.id.nemanimasyon))
        Glide.with(this).asGif().load(R.drawable.bulutmini).into(findViewById<ImageView>(R.id.bulutortusu12))
        Glide.with(this).asGif().load(R.drawable.ruzgar).into(findViewById<ImageView>(R.id.ruzgaranimasyon))
        Glide.with(this).asGif().load(R.drawable.ruzgar).into(findViewById<ImageView>(R.id.ruzgaranimasyon2))
        Glide.with(this).asGif().load(R.drawable.rainmini).into(findViewById<ImageView>(R.id.yagmuranimasyon1))
        Glide.with(this).asGif().load(R.drawable.rainmini).into(findViewById<ImageView>(R.id.bulutortusu))
        Glide.with(this).asGif().load(R.drawable.metre).into(findViewById<ImageView>(R.id.basincanimasyon))


        var chart = findViewById<ConstraintLayout>(R.id.saatlikChart)
        val chartForecast = findViewById<LineChart>(R.id.forecastLineChart)
        val grafikSwitch = findViewById<Switch>(R.id.grafikSwitch)
        val grafikswitchForecast = findViewById<Switch>(R.id.grafikSwitchForecast)
        val switch_state = getDataFromSharedPref(this, "switch_state", "switch_state") ?: "0"
        val forecast_switch_state = getDataFromSharedPref(this, "forecast_switch_state", "switch_state") ?: "0"
        val rv = findViewById<RecyclerView>(R.id.saatlik2)
        val rv_forecast = findViewById<RecyclerView>(R.id.forecast)

        grafikswitchForecast.setOnClickListener {

            if(grafikswitchForecast.isChecked == false) {
                chartForecast.animate().setDuration(1000).translationX(-resources.displayMetrics.widthPixels.toFloat()).setInterpolator(
                    AnticipateOvershootInterpolator()).withEndAction {
                    rv_forecast.translationX = resources.displayMetrics.widthPixels.toFloat()
                    rv_forecast.visibility = View.VISIBLE
                    rv_forecast.animate().translationX(0f).setDuration(1000).setInterpolator(
                        AnticipateOvershootInterpolator()).start()
                    chartForecast.visibility = View.GONE
                }.start()

                saveDataToSharedPRef(this, "forecast_switch_state", "switch_state", "false")
            }
            else{
                rv_forecast.animate().setDuration(1000).translationX(-resources.displayMetrics.widthPixels.toFloat()).setInterpolator(
                    AnticipateOvershootInterpolator()).withEndAction {
                    rv_forecast.visibility = View.GONE
                    chartForecast.translationX = resources.displayMetrics.widthPixels.toFloat()
                    chartForecast.visibility = View.VISIBLE
                    chartForecast.animate().translationX(0f).setDuration(1000).setInterpolator(
                        AnticipateOvershootInterpolator()).withEndAction {

                    }.start()

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

        val font = getDataFromSharedPref(this, "selected_font", "selected_font")
        Adapter_hourly1.fontUpdate(font)
        Adapter_hourly_detailed.fontUpdate(font)
        AdapterForecast.fontUpdate(font)


        val settings = findViewById<ImageView>(R.id.ayarlar)
        settings.setOnClickListener {
            val settingsIntent = Intent(this, ayarlar::class.java)
            startActivityForResult(settingsIntent, 1001)
        }

        val refresh = findViewById<SwipeRefreshLayout>(R.id.swipe)
        refresh.setOnRefreshListener {

            if(checkLocation(this)) {
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

        loc?.let {
            fetchWeatherData(loc.first, loc.second)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUI(weatherData : WeatherResponse) {

        viewModel.setWeatherResponse(weatherData)

        val list = convertHourlyToList(weatherData)
        val list2 = convertHourlyToList(weatherData, 1)

        var chart = findViewById<LineChart>(R.id.saatlikChartLine)
        var chartBar = findViewById<BarChart>(R.id.saatlikChartBar)
        var chartHum = findViewById<LineChart>(R.id.saatlikHumChartLine)

        if(chart != null){
            val entries = list.mapIndexedNotNull { int, list1 ->
                Entry(int.toFloat(), list1.temperature_2m.toFloat())
            }

            val entriesApp = list.mapIndexedNotNull { int, list1 ->
                Entry(int.toFloat(), list1.apparent_temperature.toFloat())
            }
            val detay = getDataFromSharedPref(this, "switch_detay", "switch_detay")

            val dataSet = LineDataSet(entries, "Sıcaklık (°C)")

            val combinedLineData = LineData()

            val combinedBarData = BarData()

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

            dataSet.apply {
                fillAlpha = alpha
                color = Color.parseColor(getColorFromSharedPref(this@MainActivity) ?: "#aa000000")
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircles(false)
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                valueTextColor = Color.parseColor("#aa000000")
                valueFormatter = valueformatter
                valueTextSize = 12f
            }

            val appDataSet = LineDataSet(entriesApp, "Hissedilen Sıcaklık (°C)").apply {
                setDrawValues(false)
                fillAlpha = alpha
                color = Color.parseColor("#6ec29a")
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircles(false)
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                valueTextColor = Color.parseColor("#aa000000")
                valueFormatter = valueformatter
                valueTextSize = 12f
            }

            if(detay == "1"){

                chartBar.visibility = View.VISIBLE
                chartHum.visibility = View.VISIBLE

                val entriesHum = list.mapIndexedNotNull { int, list1 ->
                    Entry(int.toFloat(), list1.relative_humidity_2m.toFloat())
                }

                val entriesWind = list.mapIndexedNotNull { int, list1 ->
                    BarEntry(int.toFloat(), list1.wind_speed_10m.toFloat())
                }

                val ruzHizDataSet = BarDataSet(entriesWind, "Rüzgar Hızı (km/h)").apply {

                    valueTextSize = 12f
                    color = Color.parseColor("#6ec29a")
                    valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    valueTextColor = Color.parseColor("#aa000000")
                    isHighlightEnabled = false
                }

                combinedBarData.barWidth = 0.4f

                val nemDataSet = LineDataSet(entriesHum, "Nem (%)").apply {
                    fillAlpha = alpha
                    valueTextSize = 12f
                    color = Color.parseColor("#6ec29a")
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawCircles(false)
                    valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    valueTextColor = Color.parseColor("#aa000000")
                    valueFormatter = valueformatterHum
                    isHighlightEnabled = false
                }

                chartBar.xAxis.apply {
                    axisLineColor = Color.parseColor(getColorFromSharedPref(this@MainActivity))
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    //granularity = 3f
                    isGranularityEnabled = true
                    valueFormatter = IndexAxisValueFormatter(list.map { it.time.substringAfter("T") })
                }

                chartBar.axisLeft.apply {
                    /*setDrawAxisLine(true)
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)*/
                    //granularity = 2f
                }

                chartBar.axisRight.setDrawGridLines(false)
                chartBar.axisLeft.setDrawGridLines(false)
                chartBar.xAxis.setDrawGridLines(false)

                chartHum.xAxis.apply {
                    axisLineColor = Color.parseColor(getColorFromSharedPref(this@MainActivity))
                    axisLineWidth = 2f
                    textSize = 10f
                    typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                    //granularity = 2f
                    isGranularityEnabled = true
                    valueFormatter = IndexAxisValueFormatter(list.map { it.time.substringAfter("T") })
                }

                chartHum.axisLeft.apply {

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
                chartBar.invalidate()
                chartHum.invalidate()

            }

            else{
                chartBar.visibility = View.GONE
                chartHum.visibility = View.GONE
            }

            chart.xAxis.apply {
                setDrawGridLines(false)
                axisLineColor = Color.parseColor(getColorFromSharedPref(this@MainActivity))
                axisLineWidth = 2f
                textSize = 10f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                //granularity = 3f
                isGranularityEnabled = true
                valueFormatter = IndexAxisValueFormatter(list.map { it.time.substringAfter("T") })
            }

            chart.description.isEnabled = false

            chart.axisLeft.apply {
                axisLineWidth = 2f
                textSize = 10f
                setDrawGridLines(false)
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                granularity = 3f

            }

            chart.legend.apply {
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                textColor = Color.parseColor("#aa000000")
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartHum.legend.apply {
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                textColor = Color.parseColor("#aa000000")
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartBar.legend.apply {
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                textColor = Color.parseColor("#aa000000")
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

        }

        val chartForec = findViewById<LineChart>(R.id.forecastLineChart)

        if(chartForec != null){

            val list = convertHourlyToListForecastChart(weatherData)

            val rainIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.rain)
            val sunIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.sun_74)
            val offset = 0.1f
            val entriesMax = list.mapIndexedNotNull { int, item ->
                val entry = Entry(int.toFloat() + offset, item.max_temp.toFloat())
                entry
            }

            val entriesSun = list.mapIndexedNotNull { int, item ->
                val entry = Entry(int.toFloat() + offset, (item.max_temp.toFloat() + item.min_temp.toFloat()) / 2f)
                entry.icon = if (item.yagmur == 1) rainIcon else sunIcon
                entry
            }

            val entriesMin = list.mapIndexedNotNull { int, item -> Entry(int.toFloat() + offset, item.min_temp.toFloat()) }

            val days = list.map{it.time.substringAfter(",").trim()}

            val dataMax = LineDataSet(entriesMax, "Sıcaklık Max (°C)")
            val dataMin = LineDataSet(entriesMin, "Sıcaklık Min (°C)")
            val dataSun = LineDataSet(entriesSun, "")

            dataSun.apply {
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(false)
                color = Color.parseColor("#ffffff")
                setDrawIcons(true)
            }

            val valueFormatter1 = object : ValueFormatter(){
                override fun getPointLabel(entry: Entry?): String? {
                    return if (entry?.x == 0f) "" else "${entry?.y?.toInt()}°"
                }
            }

            dataMax.apply {
                valueTextSize = 12f
                isHighlightEnabled = false
                setDrawCircles(true)
                setCircleColors(Color.parseColor(getColorFromSharedPref(this@MainActivity) ?: "#aa000000"))
                lineWidth = 2f
                circleSize = 4f
                valueTextColor = Color.parseColor("#aa000000")
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                color = Color.parseColor(getColorFromSharedPref(this@MainActivity) ?: "#aa000000")
                valueFormatter = valueFormatter1
            }

            dataMin.apply {
                valueTextSize = 12f
                isHighlightEnabled = false
                setDrawCircles(true)
                setCircleColors(Color.parseColor("#aa000000"))
                lineWidth = 2f
                circleSize = 4f
                valueTextColor = Color.parseColor("#aa000000")
                valueTypeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                color = Color.parseColor("#77000000")
                valueFormatter = valueFormatter1
            }

            chartForec.description.text = ""
            chartForec.legend.apply{
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                textSize = 12f
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                textColor = Color.parseColor("#aa000000")
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            chartForec.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days)
                typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nb)
                setDrawGridLines(false)
                textSize = 12f
            }

            chart.axisLeft.axisMaximum = entriesMax.maxOf { it.y } + 4f
            chartForec.setExtraOffsets(30f, 0f, 0f, 10f)
            chartForec.isHighlightPerTapEnabled = false

            chartForec.axisLeft.apply{
                isEnabled = false
            }

            chartForec.extraTopOffset = 10f
            chartForec.setScaleEnabled(false)
            chartForec.setDragEnabled(true)
            chartForec.setVisibleXRangeMaximum(3f)
            chartForec.moveViewToX(0f)
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

        if(!animationCheck){
            gun_animasyon = if(getDataFromSharedPref(this, "gunesli_shared", "gunesli_data") != "empty") getDataFromSharedPref(this, "gunesli_shared", "gunesli_data") else "girllaying.json"
            yagmur_animasyon = if(getDataFromSharedPref(this, "yagmurlu_shared", "yagmurlu_data") != "empty") getDataFromSharedPref(this, "yagmurlu_shared", "yagmurlu_data") else "yagmurkadin2.json"
            bulutlu_animasyon = if(getDataFromSharedPref(this, "bulutlu_shared", "bulutlu_data") != "empty") getDataFromSharedPref(this, "bulutlu_shared", "bulutlu_data") else "gokkusagi.json"
            /**/
            animationCheck = true
        }
        val switch_value = getDataFromSharedPref(this, "switch_detay", "switch_detay")
        switch_Detay = if(switch_value == "1" || switch_value == "0") switch_value else "0"

        val rv = findViewById<RecyclerView>(R.id.saatlik2)

        if (rv != null) {
            if (!isRvAttached || switch_Detay_previous != switch_Detay ) {
                switch_Detay_previous = switch_Detay
                if (switch_Detay == "0") {
                    RV_set_up_hourly(Adapter_hourly1, list)
                    val tema_renk = getColorFromSharedPref(this)
                    if (tema_renk != null) {
                        Adapter_hourly1.backGUpdate(tema_renk)
                    }
                } else {
                    RV_set_up_hourly_detailed(Adapter_hourly_detailed, list)
                    val tema_renk = getColorFromSharedPref(this)
                    if (tema_renk != null) {
                        Adapter_hourly_detailed.backGUpdate(tema_renk)
                    }
                }
                isRvAttached = true
            }
        }


        if(!is_forecast_adapter_att){
            val rv = findViewById<RecyclerView>(R.id.forecast)
            val list = convertHourlyToListForecast(weatherData)
            AdapterForecast.submitList(list)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = AdapterForecast
            rv.layoutManager = layoutManager
            getColorFromSharedPref(this)?.let { AdapterForecast.backGUpdate(it) }
            is_forecast_adapter_att = true
        }


        findViewById<TextView>(R.id.derece).text = "${weatherData.current.temperature_2m.toInt()}°"
        findViewById<TextView>(R.id.hissedilen).text =
            "Hissedilen Sıcaklık : ${weatherData.current.apparent_temperature.toInt()}°"
        findViewById<TextView>(R.id.nem).text =
            "${weatherData.current.relative_humidity_2m}${weatherData.current_units.relative_humidity_2m}"
        findViewById<TextView>(R.id.ruzgar_hizi).text =
            "${weatherData.current.wind_speed_10m} ${weatherData.current_units.wind_speed_10m}"
        findViewById<TextView>(R.id.ruzgar_yonu).text =
            "${weatherData.current.wind_direction_10m}${weatherData.current_units.wind_direction_10m}"
        findViewById<TextView>(R.id.bulutluluk).text =
            "${weatherData.current.cloud_cover}${weatherData.current_units.cloud_cover}"
        findViewById<TextView>(R.id.saganak).text =
            "${weatherData.current.showers} ${weatherData.current_units.showers}"
        findViewById<TextView>(R.id.yagmur).text =
            "${weatherData.current.rain} ${weatherData.current_units.rain}"
        findViewById<TextView>(R.id.kar).text = "${weatherData.current.snowfall} ${weatherData.current_units.snowfall}"
        findViewById<TextView>(R.id.basinc).text = "${weatherData.current.surface_pressure} ${weatherData.current_units.surface_pressure}"

        val previous_state = currentState
        currentState = updateState(weatherData)

        val mainAnimation = findViewById<LottieAnimationView>(R.id.gun)

        if (currentState != previous_state){

            var mainAnimationName = currentState
            mainAnimation.setAnimation(mainAnimationName)
            mainAnimation.playAnimation()

        }

        if(currentState == "gunes.json")
            mainAnimation.scaleType = ImageView.ScaleType.FIT_CENTER
        else
            mainAnimation.scaleType = ImageView.ScaleType.CENTER_CROP

        if(!playitonce){
            mainAnimation.setAnimation(currentState)
            mainAnimation.playAnimation()
            playitonce = true
        }

        if(check==0) {
            val maxminpair = findMaxMin(list2)
            findViewById<TextView>(R.id.maxmin).text = "Max : ${maxminpair.first}°C / Min : ${maxminpair.second}°C"
            check = 1
        }

    }

    fun getLocalTime() : String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    fun findMaxMin(list : List<HourlyList>) : Pair<Int?, Int?>{
        var max : Int? = null
        var min : Int? = null
        for (i in list){
            if(max == null || min == null){
                if(max == null)
                    max = i.temperature_2m
                if(min == null)
                    min = i.temperature_2m
            }
            if(i.temperature_2m > max)
                max = i.temperature_2m
            if (i.temperature_2m < min)
                min = i.temperature_2m
        }
        return Pair(max,min)
    }

    fun updateState(weatherData: WeatherResponse) : String{

        val time = getLocalTime().split(":")[0].toDouble()

        return if(weatherData.current.rain > 0 || weatherData.current.showers > 0){
            yagmur_animasyon
        }
        else if(weatherData.current.snowfall > 0){
            kar_animasyon
        }
        locReqCode{
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
                findViewById<TextView>(R.id.districtName).text = "${getAreaName(shPair.first, shPair.second, this).get(0)}, ${getAreaName(shPair.first, shPair.second, this).get(1)}"
                }

            val fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000).build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    val location = p0.lastLocation
                    location?.let {
                        saveLocToSharedPref(this@MainActivity, location.latitude, location.longitude)
                        findViewById<TextView>(R.id.districtName).text = "${getAreaName(location.latitude, location.longitude, this@MainActivity).get(0)}, ${getAreaName(location.latitude, location.longitude, this@MainActivity).get(1)}"

                        callback.onLocationReceived(location.latitude, location.longitude)
                        Log.d("locationeren", "Latitude: ${device_latitude}, Longitude $device_longitude")
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
                        }
                    }
                    findViewById<SwipeRefreshLayout>(R.id.swipe).isRefreshing = false
                }
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch weather: ${t.message}")
                    if(internetAlertCheck == false) {
                        customAlertDisplay(this@MainActivity)
                        internetAlertCheck = true
                    }
                    findViewById<SwipeRefreshLayout>(R.id.swipe).isRefreshing = false
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

    fun getAreaName(latitude: Double, longitude: Double, context: Context) : List<String>{
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val address = geocoder.getFromLocation(latitude, longitude, 1)
            if(address!!.isNotEmpty()){
                val city = address[0].adminArea
                val district = address[0].subAdminArea
                listOf(district, city)
            }
            else{
                emptyList()
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    fun RV_set_up_hourly(adapter : AdapterHourly, list : List<HourlyList>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter.submitList(list)
        val rv = findViewById<RecyclerView>(R.id.saatlik2)
        rv.adapter = null
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }

    fun RV_set_up_hourly_detailed(adapter : AdapterHourlyDetailed, list : List<HourlyList>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val rv = findViewById<RecyclerView>(R.id.saatlik2)
        rv.adapter = null
        adapter.submitList(list)
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertHourlyToList(weatherData: WeatherResponse, flag: Int = 0) : List<HourlyList>{
        val list = ArrayList<HourlyList>()
        var hour = LocalTime.now().hour //api 26

        if(flag == 1){
            hour = 0
        }

        for (i in hour until (hour+24)){
            val a = HourlyList(weatherData.hourly.time[i], weatherData.hourly.temperature_2m[i].toInt(), weatherData.hourly.relative_humidity_2m[i], weatherData.hourly.rain[i], weatherData.hourly.showers[i], weatherData.hourly.snowfall[i], weatherData.hourly.cloud_cover[i], weatherData.hourly.apparent_temperature[i].toInt(), weatherData.hourly.wind_speed_10m[i], weatherData.hourly.wind_direction_10m[i])
            list.add(a)
        }

        return list
    }

    fun returnMaxMin(){

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertHourlyToListForecast(weatherData: WeatherResponse) : List<DailyForecast>{
        val list = ArrayList<DailyForecast>()

        val temp = ArrayList<Double>()
        val ap_temp = ArrayList<Double>()
        val hum = ArrayList<Int>()
        val rain = ArrayList<Double>()
        val time = ArrayList<String>()

        var previousBound = 0
        for (i in weatherData.hourly.time.indices){
            var a: DailyForecast

            temp.add(weatherData.hourly.temperature_2m[i])
            ap_temp.add(weatherData.hourly.apparent_temperature[i])
            hum.add(weatherData.hourly.relative_humidity_2m[i])
            rain.add(weatherData.hourly.rain[i] + weatherData.hourly.showers[i])
            time.add(weatherData.hourly.time[i])

            val max_temp = temp.max().toInt()
            val min_temp = temp.min().toInt()
            val max_ap_temp = ap_temp.max().toInt()
            val min_ap_temp = ap_temp.min().toInt()
            val max_hum = hum.max()
            val min_hum = hum.min()
            val rain_intervals = getRainIntervals(time, rain)

            if((i + 1) % 24 == 0){
                a = DailyForecast(formatDate(weatherData.hourly.time[previousBound].substring(0,10)), max_temp.toString(), min_temp.toString(), max_ap_temp.toString(), min_ap_temp.toString(), max_hum.toString(), min_hum.toString(), rain_intervals, null )
                previousBound = i+1
                list.add(a)
                temp.clear()
                ap_temp.clear()
                hum.clear()
                rain.clear()
                time.clear()
            }
        }
        return list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertHourlyToListForecastChart(weatherData: WeatherResponse) : List<ForecastChart>{
        val list = ArrayList<ForecastChart>()

        val temp = ArrayList<Double>()
        val ap_temp = ArrayList<Double>()

        val rain = ArrayList<Double>()
        val time = ArrayList<String>()

        var previousBound = 0
        for (i in weatherData.hourly.time.indices){
            var a: ForecastChart

            temp.add(weatherData.hourly.temperature_2m[i])
            ap_temp.add(weatherData.hourly.apparent_temperature[i])

            rain.add(weatherData.hourly.rain[i] + weatherData.hourly.showers[i])
            time.add(weatherData.hourly.time[i])

            val max_temp = temp.max().toInt()
            val min_temp = temp.min().toInt()
            val rain_intervals = getRainIntervals(time, rain)

            if((i + 1) % 24 == 0){
                a = ForecastChart(formatDate(weatherData.hourly.time[previousBound].substring(0,10)), max_temp.toString(), min_temp.toString(), if(rain_intervals.isNotEmpty()) 1 else 0)
                previousBound = i+1
                list.add(a)
                temp.clear()
                ap_temp.clear()
                rain.clear()
                time.clear()
            }
        }
        return list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date: String): String {
        val localDate = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern("d MMMM, E", Locale("tr"))
        return localDate.format(formatter)
    }

    fun getRainIntervals(hourlist: ArrayList<String>, rainlist: ArrayList<Double>): String {
        val intervals = mutableListOf<Pair<String, String>>()
        var startIndex: Int? = null

        for (i in rainlist.indices) {
            if (rainlist[i] > 0) {
                if (startIndex == null) {
                    startIndex = i
                }
            } else {
                if (startIndex != null) {
                    intervals.add(Pair(hourlist[startIndex].substring(11, 16), hourlist[i - 1].substring(11, 16)))
                    startIndex = null
                }
            }
        }

        // if still raining at the end
        if (startIndex != null) {
            intervals.add(Pair(hourlist[startIndex].substring(11, 16), hourlist.last().substring(11, 16)))
        }

        return intervals.joinToString(" | ") { "${it.first} - ${it.second}" }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val mainAnimation = findViewById<LottieAnimationView>(R.id.gun)
            val selectedColor = getColorFromSharedPref(this) ?: "#7c6fde"

            var selected_bulutlu = getDataFromSharedPref(this, "bulutlu_selected", "bulutlu_data")
            if(selected_bulutlu == "empty"){
                selected_bulutlu = "gokkusagi.json"
            }

            var selected_yagmurlu = getDataFromSharedPref(this, "yagmurlu_selected", "yagmurlu_data")
            if(selected_bulutlu == "empty"){
                selected_bulutlu = "yagmurkadin.json"
            }

            var selected_font = getDataFromSharedPref(this, "selected_font", "selected_font")
            if(selected_font == "empty"){
                selected_font = "annie"
            }
            val ayar = data?.getStringExtra("ayar")

            ayar?.let{
                val loc = getLocFromSharedPref(this)
                if(loc != null){
                    fetchWeatherData(loc.first, loc.second)
                }

                var selected_gunesli = getDataFromSharedPref(this, "gunesli_shared", "gunesli_data")
                if(selected_gunesli == "empty"){
                    selected_gunesli = "girllaying.json"
                }

                if(currentState != selected_gunesli && currentState == gun_animasyon) {
                    saveDataToSharedPRef(this, "gunesli_shared", "gunesli_data", selected_gunesli)
                    val factor = 3f
                    val polator = OvershootInterpolator(factor)
                    if (currentState == gun_animasyon) {
                        gun_animasyon = selected_gunesli
                        currentState = gun_animasyon
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


                AdapterForecast.fontUpdate(selected_font)
                Adapter_hourly1.fontUpdate(selected_font)
                Adapter_hourly_detailed.fontUpdate(selected_font)

                Adapter_hourly1.backGUpdate(selectedColor)
                Adapter_hourly_detailed.backGUpdate(selectedColor)
                AdapterForecast.backGUpdate(selectedColor)
                saveColorToSharedPref(this, selectedColor)

            }


        }

    }

    fun checkLocation(context : Context) : Boolean{
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = conManager.activeNetwork ?: return false
        val capabilities = conManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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

}