package com.example.weather

import android.net.SocketKeepalive
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper.Callback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val apiurl = "https://api.open-meteo.com/v1/forecast?latitude=36.9862&longitude=35.3253&hourly=temperature_2m,relative_humidity_2m,rain,showers,snowfall&current=is_day"
        val derece = findViewById<TextView>(R.id.derece)
        val api = RetrApi.RetrofitBuilder
        val latitude = 36.9862
        val longitude = 35.3253

        api.getWeather(latitude, longitude).enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if(response.isSuccessful){
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
    fun updateUI(weatherData : WeatherResponse){
        findViewById<TextView>(R.id.derece).text = "${weatherData.current.temperature_2m}°C"
    }

}