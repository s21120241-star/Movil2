package com.example.movil2.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}