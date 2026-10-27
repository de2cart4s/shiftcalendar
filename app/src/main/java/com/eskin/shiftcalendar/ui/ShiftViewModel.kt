package com.eskin.shiftcalendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.data.DayCell
import com.eskin.shiftcalendar.data.PreferencesRepository
import com.eskin.shiftcalendar.data.ScheduleConfig
import com.eskin.shiftcalendar.data.ScheduleMode

class ShiftViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PreferencesRepository(app)

    private val _config = MutableLiveData(repo.loadConfig())
    val config: LiveData<ScheduleConfig> = _config

    private val _notes = MutableLiveData(repo.loadNotes())
    val notes: LiveData<Map<String, String>> = _notes

    private val _overrides = MutableLiveData(repo.loadOverrides())
    val overrides: LiveData<Map<String, Boolean>> = _overrides

    private val _holidays = MutableLiveData(repo.loadHolidays())
    val holidays: LiveData<Set<String>> = _holidays

    var currentYear: Int = DateUtils.todayYear(); private set
    var currentMonth: Int = DateUtils.todayMonth(); private set

    fun shiftMonth(delta: Int) {
        var m = currentMonth + delta
        var y = currentYear
        if (m < 0) { m = 11; y-- } else if (m > 11) { m = 0; y++ }
        currentMonth = m; currentYear = y
    }

    /** Статус дня БЕЗ учёта ручных переопределений */
    fun baseWorkDay(key: String): Boolean {
        val cfg = config.value ?: return true
        if (holidays.value?.contains(key) == true) return false
        return when (cfg.mode) {
            ScheduleMode.CYCLE -> {
                val n = cfg.pattern.size
                val idx = ((DateUtils.daysBetween(cfg.anchorKey, key) % n) + n) % n
                cfg.pattern[idx] == 1
            }
            ScheduleMode.WEEKLY -> cfg.weekDays[DateUtils.weekIndex(key)]
        }
    }

    /** Итоговый статус дня: переопределение > праздник > график */
    fun isWorkDay(key: String): Boolean {
        overrides.value?.get(key)?.let { return it }
        return baseWorkDay(key)
    }

    fun buildMonthCells(year: Int, month: Int): List<DayCell> {
        val cells = mutableListOf<DayCell>()
        repeat(DateUtils.firstDayOffset(year, month)) { cells.add(DayCell("", 0, false, false)) }
        val today = DateUtils.todayKey()
        for (d in 1..DateUtils.daysInMonth(year, month)) {
            val key = DateUtils.keyOf(year, month, d)
            cells.add(DayCell(key, d, isWorkDay(key), key == today))
        }
        return cells
    }

    fun monthStats(year: Int, month: Int): Triple<Int, Int, Int> {
        var work = 0; var off = 0
        for (d in 1..DateUtils.daysInMonth(year, month)) {
            if (isWorkDay(DateUtils.keyOf(year, month, d))) work++ else off++
        }
        return Triple(work, off, work * (config.value?.shiftHours ?: 8))
    }

    fun hoursByWeekday(year: Int, month: Int): List<Int> {
        val res = MutableList(7) { 0 }
        val h = config.value?.shiftHours ?: 8
        for (d in 1..DateUtils.daysInMonth(year, month)) {
            val key = DateUtils.keyOf(year, month, d)
            if (isWorkDay(key)) res[DateUtils.weekIndex(key)] += h
        }
        return res
    }

    fun saveConfig(cfg: ScheduleConfig) { repo.saveConfig(cfg); _config.value = cfg }

    fun setNote(key: String, text: String) {
        val m = notes.value.orEmpty().toMutableMap()
        if (text.isBlank()) m.remove(key) else m[key] = text
        repo.saveNotes(m); _notes.value = m
    }

    fun setOverride(key: String, work: Boolean) {
        val m = overrides.value.orEmpty().toMutableMap(); m[key] = work
        repo.saveOverrides(m); _overrides.value = m
    }

    fun removeOverride(key: String) {
        val m = overrides.value.orEmpty().toMutableMap(); m.remove(key)
        repo.saveOverrides(m); _overrides.value = m
    }

    fun addHoliday(key: String) {
        val s = holidays.value.orEmpty().toMutableSet(); s.add(key)
        repo.saveHolidays(s); _holidays.value = s
    }

    fun removeHoliday(key: String) {
        val s = holidays.value.orEmpty().toMutableSet(); s.remove(key)
        repo.saveHolidays(s); _holidays.value = s
    }

    fun resetAll() {
        repo.clear()
        _config.value = ScheduleConfig.PRESETS[0]
        _notes.value = emptyMap()
        _overrides.value = emptyMap()
        _holidays.value = PreferencesRepository.BASE_HOLIDAYS
    }
}