package com.eskin.shiftcalendar.data

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private val MONTHS = arrayOf("Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь")
    private val MONTHS_GEN = arrayOf("января","февраля","марта","апреля","мая","июня","июля","августа","сентября","октября","ноября","декабря")

    private fun utc(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    fun todayKey(): String {
        val c = utc()
        return keyOf(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }
    fun todayYear(): Int = utc().get(Calendar.YEAR)
    fun todayMonth(): Int = utc().get(Calendar.MONTH)

    fun keyOf(year: Int, monthZero: Int, day: Int): String =
        String.format(Locale.US, "%04d-%02d-%02d", year, monthZero + 1, day)

    fun parse(key: String): Calendar {
        val p = key.split("-")
        val c = utc(); c.clear()
        c.set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
        return c
    }

    fun daysBetween(from: String, to: String): Int =
        ((parse(to).timeInMillis - parse(from).timeInMillis) / 86_400_000L).toInt()

    /** 0 = понедельник ... 6 = воскресенье */
    fun weekIndex(key: String): Int = (parse(key).get(Calendar.DAY_OF_WEEK) + 5) % 7

    fun firstDayOffset(year: Int, monthZero: Int): Int = weekIndex(keyOf(year, monthZero, 1))

    fun daysInMonth(year: Int, monthZero: Int): Int {
        val c = utc(); c.clear(); c.set(year, monthZero, 1)
        return c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun monthName(monthZero: Int): String = MONTHS[monthZero]

    fun formatRu(key: String): String {
        val c = parse(key)
        return "${c.get(Calendar.DAY_OF_MONTH)} ${MONTHS_GEN[c.get(Calendar.MONTH)]} ${c.get(Calendar.YEAR)}"
    }
}