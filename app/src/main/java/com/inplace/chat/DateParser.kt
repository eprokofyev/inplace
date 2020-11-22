package com.inplace.chat

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateParser {
    private val dateFormat: DateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    private val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun getDateAsUnix(unix: Long): Long {
        val strDate = dateFormat.format(unix)
        val date = dateFormat.parse(strDate)
        return date!!.time
    }

    fun convertDateToString(date: Long): String = dateFormat.format(date)

    fun convertTimeToString(date: Long):String = timeFormat.format(date)
}