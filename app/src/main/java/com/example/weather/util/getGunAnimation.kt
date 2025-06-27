package com.example.weather.util

import android.content.Context

fun getGunAnimasyon(ctx: Context) : String{
    val darkState = getDataFromSharedPref(ctx, "dark_mode", "dark_mode")
    return if(darkState == "1")
        getDataFromSharedPref(ctx, "gunesli_shared_dark", "gunesli_data_dark").takeIf { it != "empty" } ?: "girllaying.json"
    else
        getDataFromSharedPref(ctx, "gunesli_shared", "gunesli_data").takeIf { it != "empty" } ?: "gunesmini2.json"
}