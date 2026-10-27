package com.eskin.shiftcalendar.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eskin.shiftcalendar.databinding.FragmentSettingsBinding
import com.eskin.shiftcalendar.openScreen
import com.eskin.shiftcalendar.ui.ShiftViewModel
import com.eskin.shiftcalendar.ui.schedule.CustomScheduleFragment

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подписка на изменения длительности смены
        vm.config.observe(viewLifecycleOwner) { cfg ->
            binding.hoursValue.text = "${cfg.shiftHours} ч"
        }

        // Уменьшение длительности смены (минимум 4 часа)
        binding.btnMinus.setOnClickListener {
            vm.config.value?.let { cfg ->
                val newHours = (cfg.shiftHours - 1).coerceIn(4, 12)
                vm.saveConfig(cfg.copy(shiftHours = newHours))
            }
        }

        // Увеличение длительности смены (максимум 12 часов)
        binding.btnPlus.setOnClickListener {
            vm.config.value?.let { cfg ->
                val newHours = (cfg.shiftHours + 1).coerceIn(4, 12)
                vm.saveConfig(cfg.copy(shiftHours = newHours))
            }
        }

        // Сброс всех данных
        binding.btnReset.setOnClickListener {
            vm.resetAll()
        }

        // Переход на экран настройки своего графика
        binding.btnCustomSchedule.setOnClickListener {
            openScreen(CustomScheduleFragment())
        }

        binding.btnAbout.setOnClickListener {
            openScreen(com.eskin.shiftcalendar.ui.about.AboutFragment())
        }

        binding.btnHolidays.setOnClickListener {
            openScreen(com.eskin.shiftcalendar.ui.holidays.HolidaysFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}