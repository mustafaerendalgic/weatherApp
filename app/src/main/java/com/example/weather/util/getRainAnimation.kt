package com.example.weather.util

import android.content.Context

fun getRainAnimation(ctx: Context) : String{
    val darkState = getDataFromSharedPref(ctx, "dark_mode", "dark_mode")
    return if(darkState == "1")
        getDataFromSharedPref(ctx, "yagmurlu_shared_dark", "yagmurlu_data_dark").takeIf { it != "empty" } ?: "yagmurkadin2.json"
    else
        getDataFromSharedPref(ctx, "yagmurlu_shared", "yagmurlu_data").takeIf { it != "empty" } ?: "yagmurkadin2.json"
}