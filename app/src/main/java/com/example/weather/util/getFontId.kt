package com.example.weather.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.example.weather.R

fun getFontId(ctx : Context, font: String) : Typeface? {
    return when(font){
        "indie" -> ResourcesCompat.getFont(ctx, R.font.indie)
        "nb" -> ResourcesCompat.getFont(ctx, R.font.nb)
        "annie" -> ResourcesCompat.getFont(ctx, R.font.annie)
        "single" -> ResourcesCompat.getFont(ctx, R.font.single)
        "zilla" -> ResourcesCompat.getFont(ctx, R.font.zilla)
        "inter" -> ResourcesCompat.getFont(ctx, R.font.inter)
        else -> ResourcesCompat.getFont(ctx, R.font.annie)
    }
}