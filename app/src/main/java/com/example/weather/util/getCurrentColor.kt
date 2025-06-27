package com.example.weather.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.weather.R

fun returnTheCurrentColor(ctx: Context): String{

    val isdark = getDataFromSharedPref(ctx, "dark_mode", "dark_mode").takeIf { it != "empty" } ?: "0"

    var colorDark = getColorFromSharedPrefForDarkMode(ctx) ?: colorIntToHex(ContextCompat.getColor(ctx, R.color.settingsCol3)).lowercase()

    var colorLight = getColorFromSharedPref(ctx) ?: colorIntToHex(ContextCompat.getColor(ctx, R.color.settingsCol2)).lowercase()

    if(isdark == "1")
        return colorDark
    else
        return colorLight

}