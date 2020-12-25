package com.inplace.chat

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateParser {
    private val dateFormatOfCurrentYear: DateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    private val dateFormatOfPrevYear: DateFormat = SimpleDateFormat("dd MM yyyy", Locale.US)
    private val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun getDateAsUnix(unix: Long): Long {
        val strDate = dateFormatOfCurrentYear.format(unix)
        val date = dateFormatOfCurrentYear.parse(strDate)
        return date!!.time
    }

    fun convertDateToString(date: Long): String = dateFormatOfCurrentYear.format(date)

    fun convertDateOfPreviousYearToString(date: Long): String = dateFormatOfPrevYear.format(date)

    fun convertTimeToString(date: Long): String = timeFormat.format(date)

    fun getNowDate(): Long = Calendar.getInstance().timeInMillis
}