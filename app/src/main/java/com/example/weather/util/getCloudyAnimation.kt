package com.example.weather.util

import android.content.Context

fun getCloudyAnimation(ctx: Context) : String {
    val darkState = getDataFromSharedPref(ctx, "dark_mode", "dark_mode")
    return if(darkState == "1")
        getDataFromSharedPref(ctx, "bulutlu_shared_dark", "bulutlu_data_dark").takeIf { it != "empty" } ?: "gokkusagi.json"
    else
        getDataFromSharedPref(ctx, "bulutlu_shared", "bulutlu_data").takeIf { it != "empty" } ?: "gokkusagi.json"
}