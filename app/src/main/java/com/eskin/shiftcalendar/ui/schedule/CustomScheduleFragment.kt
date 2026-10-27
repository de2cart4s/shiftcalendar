package com.eskin.shiftcalendar.ui.schedule

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.data.ScheduleConfig
import com.eskin.shiftcalendar.data.ScheduleMode
import com.eskin.shiftcalendar.databinding.FragmentCustomScheduleBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import java.util.Calendar

class CustomScheduleFragment : Fragment() {

    private var _binding: FragmentCustomScheduleBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()

    private var selectedAnchorKey: String = DateUtils.todayKey()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentConfig = vm.config.value ?: ScheduleConfig.PRESETS[0]
        restoreFromConfig(currentConfig)
        updateAnchorDateLabel()

        binding.radioMode.setOnCheckedChangeListener { _, checkedId ->
            val isWeekly = checkedId == com.eskin.shiftcalendar.R.id.radio_weekly
            binding.weeklyContainer.visibility = if (isWeekly) View.VISIBLE else View.GONE
            binding.cycleContainer.visibility = if (isWeekly) View.GONE else View.VISIBLE
        }

        binding.btnAnchorDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val mode = if (binding.radioWeekly.isChecked) ScheduleMode.WEEKLY else ScheduleMode.CYCLE

            val config = when (mode) {
                ScheduleMode.WEEKLY -> {
                    val weekDays = listOf(
                        binding.dayMon.isChecked,
                        binding.dayTue.isChecked,
                        binding.dayWed.isChecked,
                        binding.dayThu.isChecked,
                        binding.dayFri.isChecked,
                        binding.daySat.isChecked,
                        binding.daySun.isChecked
                    )
                    if (weekDays.none { it }) {
                        Toast.makeText(requireContext(), "Выберите хотя бы один рабочий день", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val hours = binding.hoursInput.text.toString().toIntOrNull()?.coerceIn(1, 24) ?: 8
                    ScheduleConfig(
                        name = "Свой (по дням)",
                        mode = ScheduleMode.WEEKLY,
                        pattern = emptyList(),
                        weekDays = weekDays,
                        anchorKey = DateUtils.todayKey(),
                        shiftHours = hours
                    )
                }
                ScheduleMode.CYCLE -> {
                    val workDays = binding.cycleWork.text.toString().toIntOrNull()?.coerceIn(1, 14) ?: 2
                    val offDays = binding.cycleOff.text.toString().toIntOrNull()?.coerceIn(1, 14) ?: 2
                    val pattern = List(workDays) { 1 } + List(offDays) { 0 }
                    val hours = binding.hoursInput.text.toString().toIntOrNull()?.coerceIn(1, 24) ?: 8
                    ScheduleConfig(
                        name = "Свой (цикл $workDays/$offDays)",
                        mode = ScheduleMode.CYCLE,
                        pattern = pattern,
                        weekDays = List(7) { true },
                        anchorKey = selectedAnchorKey,
                        shiftHours = hours
                    )
                }
            }

            vm.saveConfig(config)
            Toast.makeText(requireContext(), "График сохранён", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun showDatePicker() {
        val cal = DateUtils.parse(selectedAnchorKey)
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                selectedAnchorKey = DateUtils.keyOf(y, m, d)
                updateAnchorDateLabel()
            },
            year, month, day
        ).show()
    }

    private fun updateAnchorDateLabel() {
        binding.btnAnchorDate.text = DateUtils.formatRu(selectedAnchorKey)
    }

    private fun restoreFromConfig(config: ScheduleConfig) {
        when (config.mode) {
            ScheduleMode.WEEKLY -> {
                binding.radioWeekly.isChecked = true
                binding.radioCycle.isChecked = false
                binding.weeklyContainer.visibility = View.VISIBLE
                binding.cycleContainer.visibility = View.GONE
                binding.dayMon.isChecked = config.weekDays[0]
                binding.dayTue.isChecked = config.weekDays[1]
                binding.dayWed.isChecked = config.weekDays[2]
                binding.dayThu.isChecked = config.weekDays[3]
                binding.dayFri.isChecked = config.weekDays[4]
                binding.daySat.isChecked = config.weekDays[5]
                binding.daySun.isChecked = config.weekDays[6]
            }
            ScheduleMode.CYCLE -> {
                binding.radioWeekly.isChecked = false
                binding.radioCycle.isChecked = true
                binding.weeklyContainer.visibility = View.GONE
                binding.cycleContainer.visibility = View.VISIBLE
                val workDays = config.pattern.count { it == 1 }
                val offDays = config.pattern.count { it == 0 }
                binding.cycleWork.setText(workDays.toString())
                binding.cycleOff.setText(offDays.toString())
                selectedAnchorKey = config.anchorKey
            }
        }
        binding.hoursInput.setText(config.shiftHours.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}