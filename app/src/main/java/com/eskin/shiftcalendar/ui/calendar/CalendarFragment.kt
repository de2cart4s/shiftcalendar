package com.eskin.shiftcalendar.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.data.ScheduleConfig
import com.eskin.shiftcalendar.databinding.FragmentCalendarBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import com.eskin.shiftcalendar.openScreen
import com.eskin.shiftcalendar.ui.schedule.SchedulePickerFragment
import com.eskin.shiftcalendar.ui.day.DayDetailsSheet
import com.eskin.shiftcalendar.ui.notes.NotesListFragment

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()
    private lateinit var adapter: CalendarAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CalendarAdapter { key -> showDayDialog(key) }
        binding.calendarGrid.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarGrid.adapter = adapter

        binding.btnPrevMonth.setOnClickListener { vm.shiftMonth(-1); render() }
        binding.btnNextMonth.setOnClickListener { vm.shiftMonth(1); render() }
        binding.btnChangeSchedule.setOnClickListener { showScheduleDialog() }
        binding.btnNotes.setOnClickListener { openScreen(NotesListFragment()) }

        vm.config.observe(viewLifecycleOwner) { render() }
        vm.overrides.observe(viewLifecycleOwner) { render() }
        render()
    }

    private fun render() {
        val year = vm.currentYear
        val month = vm.currentMonth
        binding.monthLabel.text = "${DateUtils.monthName(month)} $year"
        adapter.submit(vm.buildMonthCells(year, month))
        val s = vm.monthStats(year, month)
        binding.statWork.text = s.first.toString()
        binding.statHours.text = "${s.third} ч"
        binding.statOff.text = s.second.toString()
        binding.btnChangeSchedule.text = "График: ${vm.config.value?.name ?: "-"} · изменить"
    }

    private fun showDayDialog(key: String) {
        DayDetailsSheet.newInstance(key).show(requireActivity().supportFragmentManager, "day_details")
    }

    private fun showScheduleDialog() {
        openScreen(SchedulePickerFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}