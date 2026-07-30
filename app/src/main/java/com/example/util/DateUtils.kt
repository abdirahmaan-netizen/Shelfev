package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH)

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun timeAgo(timestamp: Long, currentTimeMs: Long = System.currentTimeMillis()): String {
        val diffSeconds = (currentTimeMs - timestamp) / 1000
        if (diffSeconds < 5) return "hadda"
        if (diffSeconds < 60) return "$diffSeconds ilbiriqsi ka hor"

        val mins = diffSeconds / 60
        if (mins < 60) return "$mins daqiiqo ka hor"

        val hrs = mins / 60
        if (hrs < 24) return "$hrs saac ka hor"

        val days = hrs / 24
        return "$days maalmood ka hor"
    }
}
