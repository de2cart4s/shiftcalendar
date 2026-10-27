package com.eskin.shiftcalendar.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("shift_calendar", Context.MODE_PRIVATE)

    fun loadConfig(): ScheduleConfig {
        val raw = prefs.getString(KEY_CONFIG, null) ?: return ScheduleConfig.PRESETS[0]
        return try {
            val o = JSONObject(raw)
            val loadedMode = runCatching { ScheduleMode.valueOf(o.optString("mode", "CYCLE")) }
                .getOrDefault(ScheduleMode.CYCLE)

            val pattern = mutableListOf<Int>()
            o.optJSONArray("pattern")?.let { arr ->
                for (i in 0 until arr.length()) pattern.add(arr.getInt(i))
            }

            val week = mutableListOf<Boolean>()
            o.optJSONArray("weekDays")?.let { arr ->
                for (i in 0 until arr.length()) week.add(arr.getBoolean(i))
            }

            // Для CYCLE нужен непустой pattern; для WEEKLY — корректный weekDays
            val finalPattern = if (loadedMode == ScheduleMode.CYCLE && pattern.isEmpty()) {
                listOf(1, 1, 1, 1, 1, 0, 0)
            } else pattern

            val finalWeek = if (week.size == 7) {
                week
            } else {
                // Значения по умолчанию: Пн-Пт рабочие (российский 5/2)
                listOf(true, true, true, true, true, false, false)
            }

            ScheduleConfig(
                name = o.optString("name", "5/2"),
                mode = loadedMode,
                pattern = finalPattern,
                weekDays = finalWeek,
                anchorKey = o.optString("anchorKey", DateUtils.todayKey()),
                shiftHours = o.optInt("shiftHours", 8)
            )
        } catch (e: Exception) {
            ScheduleConfig.PRESETS[0]
        }
    }

    fun saveConfig(c: ScheduleConfig) {
        val o = JSONObject()
        o.put("name", c.name)
        o.put("mode", c.mode.name)
        o.put("pattern", JSONArray(c.pattern))
        o.put("weekDays", JSONArray(c.weekDays))
        o.put("anchorKey", c.anchorKey)
        o.put("shiftHours", c.shiftHours)
        prefs.edit().putString(KEY_CONFIG, o.toString()).apply()
    }

    fun loadNotes(): Map<String, String> {
        val raw = prefs.getString(KEY_NOTES, null) ?: return emptyMap()
        val o = JSONObject(raw)
        val res = mutableMapOf<String, String>()
        for (k in o.keys()) res[k] = o.getString(k)
        return res
    }

    fun saveNotes(m: Map<String, String>) {
        prefs.edit().putString(KEY_NOTES, JSONObject(m).toString()).apply()
    }

    fun loadOverrides(): Map<String, Boolean> {
        val raw = prefs.getString(KEY_OVERRIDES, null) ?: return emptyMap()
        val o = JSONObject(raw)
        val res = mutableMapOf<String, Boolean>()
        for (k in o.keys()) res[k] = o.getBoolean(k)
        return res
    }

    fun saveOverrides(m: Map<String, Boolean>) {
        prefs.edit().putString(KEY_OVERRIDES, JSONObject(m).toString()).apply()
    }

    fun clear() = prefs.edit().clear().apply()

    fun loadHolidays(): Set<String> {
        if (!prefs.contains(KEY_HOLIDAYS)) {
            saveHolidays(BASE_HOLIDAYS)
            return BASE_HOLIDAYS
        }
        return prefs.getStringSet(KEY_HOLIDAYS, emptySet()) ?: emptySet()
    }

    fun saveHolidays(set: Set<String>) {
        prefs.edit().putStringSet(KEY_HOLIDAYS, set).apply()
    }

    companion object {
        const val KEY_HOLIDAYS = "holidays"

        val BASE_HOLIDAYS = setOf(
            "2026-01-01", "2026-01-02", "2026-01-03", "2026-01-04",
            "2026-01-05", "2026-01-06", "2026-01-07", "2026-01-08",
            "2026-02-23", "2026-03-08", "2026-05-01", "2026-05-09",
            "2026-06-12", "2026-11-04"
        )
        const val KEY_CONFIG = "config"
        const val KEY_NOTES = "notes"
        const val KEY_OVERRIDES = "overrides"
    }
}