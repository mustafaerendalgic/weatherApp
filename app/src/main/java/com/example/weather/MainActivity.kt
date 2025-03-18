package com.example.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.SocketKeepalive
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper.Callback
import androidx.lifecycle.lifecycleScope
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
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var check = 0
    private var device_latitude : Double? = null
    private var device_longitude : Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            insets
        }



        /*val apiurl = "https://api.open-meteo.com/v1/forecast?latitude=36.9862&longitude=35.3253&hourly=temperature_2m,relative_humidity_2m,rain,showers,snowfall&current=is_day"*/
        val derece = findViewById<TextView>(R.id.derece)

        checkLocPerm()

    }
    fun updateUI(weatherData : WeatherResponse){
        val adapter
        findViewById<TextView>(R.id.derece).text = "${weatherData.current.temperature_2m}°C"
        findViewById<TextView>(R.id.hissedilen).text = "Hissedilen Sıcaklık : ${weatherData.current.apparent_temperature}°C"
        findViewById<TextView>(R.id.nem).text = "Nem : ${weatherData.current.relative_humidity_2m} ${weatherData.current_units.relative_humidity_2m}"
        findViewById<TextView>(R.id.ruzgar_hizi).text = "Rüzgar hızı : ${weatherData.current.wind_speed_10m} ${weatherData.current_units.wind_speed_10m}"
        findViewById<TextView>(R.id.ruzgar_yonu).text = "Rüzgar yönü : ${weatherData.current.wind_direction_10m} ${weatherData.current_units.wind_direction_10m}"
        findViewById<TextView>(R.id.bulutluluk).text = "Bulut örtüsü yüzdesi : ${weatherData.current.cloud_cover} ${weatherData.current_units.cloud_cover}"
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

            val shPair = getLocFromSharedPref()

            shPair?.let {
                callback.onLocationReceived(shPair.first, shPair.second)
                findViewById<TextView>(R.id.districtName).text = "${getAreaName(shPair.first, shPair.second, this).get(0)}, ${getAreaName(shPair.first, shPair.second, this).get(1)}"
                /*findViewById<TextView>(R.id.cityName).text = getAreaName(shPair.first, shPair.second, this).get(1)*/}

            val fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    val location = p0.lastLocation
                    location?.let {
                        saveLocToSharedPref(location.latitude, location.longitude)
                        findViewById<TextView>(R.id.districtName).text = "${getAreaName(location.latitude, location.longitude, this@MainActivity).get(0)}, ${getAreaName(location.latitude, location.longitude, this@MainActivity).get(1)}"
                        /*findViewById<TextView>(R.id.cityName).text = getAreaName(location.latitude, location.longitude, this@MainActivity).get(1)*/
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
                }
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch weather: ${t.message}")
                }
            })
    }

    private fun saveLocToSharedPref(latitude: Double, longitude: Double){

        val sharedPref = getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("latitude", latitude.toString())
        editor.putString("longitude", longitude.toString())
        editor.apply()

    }

    private fun getLocFromSharedPref() : Pair<Double, Double>? {
        val sharedPref = getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

        val latitude = sharedPref.getString("latitude", null)?.toDoubleOrNull()
        val longitude = sharedPref.getString("longitude", null)?.toDoubleOrNull()

        return if (latitude != null && longitude != null){
            Pair(latitude, longitude)
        }
        else{
            null
        }
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

    fun getHourlyItems(weatherResponse: WeatherResponse) : List<Pair<String, String>>{
        val list = ArrayList<Pair<String, String>>()
        for (i in 0 until 24){
            val pair = Pair<String, String>(weatherResponse.hourly.time[i], weatherResponse.hourly.temperature_2m[i].toString())
            list.add(pair)
        }
        return list
    }

}