package com.example.weather.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun changeTheLanguage(ctx: Context){
    val dil = getDataFromSharedPref(ctx, "dil", "dil").takeIf { it != "empty"}
    if(dil == "tr"){
        val localeList = LocaleListCompat.forLanguageTags("tr")
        AppCompatDelegate.setApplicationLocales(localeList)

    }
    else if(dil == "en"){
        val localeList = LocaleListCompat.forLanguageTags("en")
        AppCompatDelegate.setApplicationLocales(localeList)

    }
}