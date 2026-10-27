package com.eskin.shiftcalendar.data

enum class ScheduleMode { CYCLE, WEEKLY }

data class ScheduleConfig(
    val name: String,
    val mode: ScheduleMode,
    val pattern: List<Int>,        // для CYCLE: 1 = рабочий, 0 = выходной
    val weekDays: List<Boolean>,   // для WEEKLY: Пн-Вс
    val anchorKey: String,
    val shiftHours: Int
) {
    val workCount: Int get() = when (mode) {
        ScheduleMode.CYCLE -> pattern.sum()
        ScheduleMode.WEEKLY -> weekDays.count { it }
    }
    val period: Int get() = if (mode == ScheduleMode.CYCLE) pattern.size else 7

    val label: String get() = "$workCount раб. / ${period - workCount} вых."

    /** Описание для диалога выбора */
    val description: String get() = when (mode) {
        ScheduleMode.WEEKLY -> {
            val names = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            val work = (0..6).filter { weekDays[it] }.map { names[it] }
            "выходные: ${names.filterIndexed { i, _ -> !weekDays[i] }.joinToString(", ")}"
        }
        ScheduleMode.CYCLE -> "цикл ${pattern.size} дн. (плавающий)"
    }

    companion object {
        val PRESETS: List<ScheduleConfig> = listOf(
            // 5/2
            ScheduleConfig(
                name = "5/2",
                mode = ScheduleMode.WEEKLY,
                pattern = emptyList(),
                weekDays = listOf(true, true, true, true, true, false, false),
                anchorKey = DateUtils.todayKey(),
                shiftHours = 8
            ),
            // 2/2 — плавающий цикл (2 рабочих, 2 выходных)
            ScheduleConfig(
                name = "2/2",
                mode = ScheduleMode.CYCLE,
                pattern = listOf(1, 1, 0, 0),
                weekDays = List(7) { true },
                anchorKey = DateUtils.todayKey(),
                shiftHours = 8
            ),
            // 3/3 — плавающий цикл (3 рабочих, 3 выходных)
            ScheduleConfig(
                name = "3/3",
                mode = ScheduleMode.CYCLE,
                pattern = listOf(1, 1, 1, 0, 0, 0),
                weekDays = List(7) { true },
                anchorKey = DateUtils.todayKey(),
                shiftHours = 8
            ),
            // 6/1 — шестидневка: Пн-Сб рабочие, Вс выходной
            ScheduleConfig(
                name = "6/1",
                mode = ScheduleMode.WEEKLY,
                pattern = emptyList(),
                weekDays = listOf(true, true, true, true, true, true, false),
                anchorKey = DateUtils.todayKey(),
                shiftHours = 8
            )
        )
    }
}