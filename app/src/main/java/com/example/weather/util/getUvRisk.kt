package com.example.weather.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.weather.R

fun getUvRiskLevel(uvIndex: Double, ctx: Context): String {
    return when {
        uvIndex < 0 -> ContextCompat.getString(ctx, R.string.uvGecersiz)
        uvIndex < 3 -> ContextCompat.getString(ctx, R.string.uvDusuk)
        uvIndex < 6 -> ContextCompat.getString(ctx, R.string.uvOrta)
        uvIndex < 8 -> ContextCompat.getString(ctx, R.string.uvYuksek)
        uvIndex < 11 -> ContextCompat.getString(ctx, R.string.uvGecersiz)
        else -> ContextCompat.getString(ctx, R.string.uvAsiri)
    }
}