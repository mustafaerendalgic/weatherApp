package com.example.weather.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun resizeDrawable(context: Context, drawableResId: Int, width: Int, height: Int): Drawable {
    val original = ContextCompat.getDrawable(context, drawableResId)!!
    val bitmap = (original as BitmapDrawable).bitmap
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
    return BitmapDrawable(context.resources, scaledBitmap)
}
