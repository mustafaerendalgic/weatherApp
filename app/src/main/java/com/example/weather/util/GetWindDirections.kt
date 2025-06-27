package com.example.weather.util

import android.content.Context
import com.example.weather.R

fun getWindDirectionSimple(context: Context, degree: Double): String {
    return when (degree) {
        in 337.5..360.0, in 0.0..22.5 ->
            context.getString(R.string.wind_direction_north)
        in 22.5..67.5 ->
            context.getString(R.string.wind_direction_northeast)
        in 67.5..112.5 ->
            context.getString(R.string.wind_direction_east)
        in 112.5..157.5 ->
            context.getString(R.string.wind_direction_southeast)
        in 157.5..202.5 ->
            context.getString(R.string.wind_direction_south)
        in 202.5..247.5 ->
            context.getString(R.string.wind_direction_southwest)
        in 247.5..292.5 ->
            context.getString(R.string.wind_direction_west)
        in 292.5..337.5 ->
            context.getString(R.string.wind_direction_northwest)
        else ->
            context.getString(R.string.wind_direction_unknown)
    }
}