package com.example.weather.util

import com.example.weather.data.HourlyList


fun findMaxMin(list : List<HourlyList>) : Pair<Int?, Int?>{
    var max : Int? = null
    var min : Int? = null
    for (i in list){
        if(max == null || min == null){
            if(max == null)
                max = i.temperature_2m
            if(min == null)
                min = i.temperature_2m
        }
        if(i.temperature_2m > max)
            max = i.temperature_2m
        if (i.temperature_2m < min)
            min = i.temperature_2m
    }
    return Pair(max,min)
}
