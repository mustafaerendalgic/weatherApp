package com.example.weather.util

fun getRainIntervals(hourlist: ArrayList<String>, rainlist: ArrayList<Double>): String {
    val intervals = mutableListOf<Pair<String, String>>()
    var startIndex: Int? = null

    for (i in rainlist.indices) {
        if (rainlist[i] > 0) {
            if (startIndex == null) {
                startIndex = i
            }
        } else {
            if (startIndex != null) {
                intervals.add(Pair(hourlist[startIndex].substring(11, 16), hourlist[i - 1].substring(11, 16)))
                startIndex = null
            }
        }
    }

    // if still raining at the end
    if (startIndex != null) {
        intervals.add(Pair(hourlist[startIndex].substring(11, 16), hourlist.last().substring(11, 16)))
    }

    return intervals.joinToString(" | ") { "${it.first} - ${it.second}" }
}