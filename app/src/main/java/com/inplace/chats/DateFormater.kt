package com.inplace.chats


import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateFormater {

    private val dateFormatOfCurrentYear: DateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val dateFormatOfPrevYear: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun getDateAsUnix(unix: Long): Long {
        val strDate = dateFormatOfCurrentYear.format(unix)
        val date = dateFormatOfCurrentYear.parse(strDate)
        return date!!.time
    }

    fun getDayOfWeek(date: Long): Int {
        return Date(date).day
    }

    fun getYear(date: Long): Int {
        return Date(date).year
    }

    fun getMonth(date: Long): Int {
        return Date(date).month
    }


    fun getDate(date: Long): Int {
        return Date(date).date
    }


    fun convertDateForPreviousYearToString(date: Long): String = dateFormatOfPrevYear.format(date)

    fun convertDateForCurrentYearToString(date: Long): String = dateFormatOfCurrentYear.format(date)

    fun convertDateForCurrentDayToString(date: Long): String = timeFormat.format(date)

    fun convertDateOfPreviousYearToString(date: Long): String = dateFormatOfPrevYear.format(date)


    fun convertTimeToString(date: Long): String = timeFormat.format(date)

    fun getNowDate(): Long = Calendar.getInstance().timeInMillis

}