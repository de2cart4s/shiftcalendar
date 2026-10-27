package com.eskin.shiftcalendar.ui.schedule

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.data.ScheduleConfig
import com.eskin.shiftcalendar.data.ScheduleMode
import com.eskin.shiftcalendar.databinding.FragmentSchedulePickerBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import com.eskin.shiftcalendar.openScreen
import java.util.Calendar

class SchedulePickerFragment : Fragment() {

    private var _binding: FragmentSchedulePickerBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()
    private lateinit var adapter: SchedulePreviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSchedulePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SchedulePreviewAdapter { config ->
            when (config.mode) {
                ScheduleMode.WEEKLY -> {
                    // Для графиков по дням недели дата не нужна
                    vm.saveConfig(config.copy(anchorKey = DateUtils.todayKey()))
                    requireActivity().supportFragmentManager.popBackStack()
                }
                ScheduleMode.CYCLE -> {
                    // Для циклических графиков — диалог выбора даты первой смены
                    showAnchorDatePicker(config)
                }
            }
        }

        binding.scheduleList.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleList.adapter = adapter
        adapter.submit(ScheduleConfig.PRESETS)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnCustom.setOnClickListener {
            openScreen(CustomScheduleFragment())
        }
    }

    private fun showAnchorDatePicker(config: ScheduleConfig) {
        val today = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val anchorKey = DateUtils.keyOf(year, month, day)
                vm.saveConfig(config.copy(anchorKey = anchorKey))
                requireActivity().supportFragmentManager.popBackStack()
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Выберите дату первой смены")
            setMessage("От этой даты начнётся отсчёт цикла ${config.name}")
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}