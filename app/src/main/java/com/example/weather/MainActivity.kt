package com.example.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.SocketKeepalive
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper.Callback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.weather.util.getLocFromSharedPref
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import com.example.weather.util.*

class MainActivity : AppCompatActivity() {
    private var check = 0

    lateinit var  weatherData1 : WeatherResponse

    private var device_latitude : Double? = null
    private var device_longitude : Double? = null
    private var isRvAttached = false
    private var Adapter_hourly1 = Adapter_hourly("#7c6fde", this, "annie")
    private var Adapter_hourly_detailed = Adapter_hourly_detailed("#7c6fde", this, "annie")

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*val apiurl = "https://api.open-meteo.com/v1/forecast?latitude=36.9862&longitude=35.3253&hourly=temperature_2m,relative_humidity_2m,rain,showers,snowfall&current=is_day"*/

        checkLocPerm()

        Glide.with(this).asGif().load(R.drawable.nem).into(findViewById<ImageView>(R.id.nemanimasyon))
        Glide.with(this).asGif().load(R.drawable.bulutmini).into(findViewById<ImageView>(R.id.bulutortusu12))
        Glide.with(this).asGif().load(R.drawable.ruzgar).into(findViewById<ImageView>(R.id.ruzgaranimasyon))
        Glide.with(this).asGif().load(R.drawable.ruzgar).into(findViewById<ImageView>(R.id.ruzgaranimasyon2))
        Glide.with(this).asGif().load(R.drawable.rainmini).into(findViewById<ImageView>(R.id.yagmuranimasyon1))
        Glide.with(this).asGif().load(R.drawable.rainmini).into(findViewById<ImageView>(R.id.bulutortusu))
        Glide.with(this).asGif().load(R.drawable.metre).into(findViewById<ImageView>(R.id.basincanimasyon))


        val font = getDataFromSharedPref(this, "font_selected", "font_selected")
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
            else{
                customAlertDisplay(this)
                refresh.isRefreshing = false
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

        val list = convertHourlyToList(weatherData)
        val list2 = convertHourlyToList(weatherData, 1)

        if(!animationCheck){
            gun_animasyon = if(getDataFromSharedPref(this, "gunesli_shared", "gunesli_data") != "empty") getDataFromSharedPref(this, "gunesli_shared", "gunesli_data") else "girllaying.json"
            yagmur_animasyon = if(getDataFromSharedPref(this, "yagmurlu_shared", "yagmurlu_data") != "empty") getDataFromSharedPref(this, "yagmurlu_shared", "yagmurlu_data") else "yagmurkadin2.json"
            bulutlu_animasyon = if(getDataFromSharedPref(this, "bulutlu_shared", "bulutlu_data") != "empty") getDataFromSharedPref(this, "bulutlu_shared", "bulutlu_data") else "gokkusagi.json"
            /**/
            animationCheck = true
        }
        val switch_value = getDataFromSharedPref(this, "switch_detay", "switch_detay")
        switch_Detay = if(switch_value == "1" || switch_value == "0") switch_value else "0"

        if (!isRvAttached || switch_Detay_previous != switch_Detay) {
            switch_Detay_previous = switch_Detay
            if(switch_Detay == "0") {
                RV_set_up_hourly(Adapter_hourly1, list)
                val tema_renk = getColorFromSharedPref(this)
                if (tema_renk != null) {
                    Adapter_hourly1.backGUpdate(tema_renk)
                }
            }
            else{
                RV_set_up_hourly_detailed(Adapter_hourly_detailed, list)
                val tema_renk = getColorFromSharedPref(this)
                if (tema_renk != null) {
                    Adapter_hourly_detailed.backGUpdate(tema_renk)
                }
            }
            isRvAttached = true
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

    fun findMaxMin(list : List<hourly_list>) : Pair<Int?, Int?>{
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
        else if (time >= 19){
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
                            weatherData1 = weatherData
                        }
                    }
                    findViewById<SwipeRefreshLayout>(R.id.swipe).isRefreshing = false
                }
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch weather: ${t.message}")
                    customAlertDisplay(this@MainActivity)
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

    fun RV_set_up_hourly(adapter : Adapter_hourly, list : List<hourly_list>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter.submitList(list)
        val rv = findViewById<RecyclerView>(R.id.saatlik)
        rv.adapter = null
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }

    fun RV_set_up_hourly_detailed(adapter : Adapter_hourly_detailed, list : List<hourly_list>){
        val layManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val rv = findViewById<RecyclerView>(R.id.saatlik)
        rv.adapter = null
        adapter.submitList(list)
        rv.adapter = adapter
        rv.layoutManager = layManager
        val itemDecorations = rv.itemDecorationCount
        if(itemDecorations == 0)
            rv.addItemDecoration(Item_Decoration(35))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertHourlyToList(weatherData: WeatherResponse, flag: Int = 0) : List<hourly_list>{
        val list = ArrayList<hourly_list>()
        var hour = LocalTime.now().hour //api 26

        if(flag == 1){
            hour = 0
        }

        for (i in hour until (hour+24)){
            val a = hourly_list(weatherData.hourly.time[i], weatherData.hourly.temperature_2m[i].toInt(), weatherData.hourly.relative_humidity_2m[i], weatherData.hourly.rain[i], weatherData.hourly.showers[i], weatherData.hourly.snowfall[i], weatherData.hourly.cloud_cover[i], weatherData.hourly.apparent_temperature[i].toInt(), weatherData.hourly.wind_speed_10m[i], weatherData.hourly.wind_direction_10m[i])
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
            alert.dismiss()
        }
        alert.show()
    }

}