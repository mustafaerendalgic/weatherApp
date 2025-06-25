package com.example.weather.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun checkConnection(context : Context) : Boolean{
    val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = conManager.activeNetwork ?: return false
    val capabilities = conManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}