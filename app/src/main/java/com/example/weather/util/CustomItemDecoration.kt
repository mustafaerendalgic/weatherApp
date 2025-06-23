package com.example.weather.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class Item_Decoration(val space : Int) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if(position == 0 ){
            outRect.left = space
            outRect.right = space
        }
        else{
            outRect.right = space
        }

    }
}