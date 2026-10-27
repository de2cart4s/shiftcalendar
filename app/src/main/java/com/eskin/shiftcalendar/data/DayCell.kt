package com.eskin.shiftcalendar.data

data class DayCell(val key: String, val day: Int, val work: Boolean, val isToday: Boolean) {
    val isEmpty: Boolean get() = day == 0
}