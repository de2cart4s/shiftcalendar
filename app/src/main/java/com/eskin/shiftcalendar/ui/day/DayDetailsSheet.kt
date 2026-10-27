package com.eskin.shiftcalendar.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.eskin.shiftcalendar.R
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.databinding.SheetDayDetailsBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DayDetailsSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_KEY = "day_key"
        fun newInstance(key: String) = DayDetailsSheet().apply {
            arguments = Bundle().apply { putString(ARG_KEY, key) }
        }
    }

    private var _binding: SheetDayDetailsBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()
    private var dayKey: String = ""
    private var selectedWork: Boolean = true

    override fun getTheme(): Int = R.style.Theme_ShiftCalendar_BottomSheet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetDayDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayKey = arguments?.getString(ARG_KEY) ?: DateUtils.todayKey()
        selectedWork = vm.isWorkDay(dayKey)
        render()

        binding.chipWork.setOnClickListener { selectedWork = true; render() }
        binding.chipOff.setOnClickListener { selectedWork = false; render() }

        binding.sheetNote.setText(vm.notes.value?.get(dayKey).orEmpty())

        binding.sheetSave.setOnClickListener {
            val base = vm.baseWorkDay(dayKey)
            if (selectedWork != base) vm.setOverride(dayKey, selectedWork)
            else vm.removeOverride(dayKey)
            vm.setNote(dayKey, binding.sheetNote.text.toString())
            dismiss()
        }
    }

    private fun render() {
        binding.sheetDate.text = DateUtils.formatRu(dayKey)
        binding.sheetStatus.text = if (vm.holidays.value?.contains(dayKey) == true)
            "Праздник (выходной)" else "По графику: ${if (vm.baseWorkDay(dayKey)) "рабочий" else "выходной"}"
        binding.sheetHours.text = "Часов в смене: ${vm.config.value?.shiftHours ?: 8}"
        binding.chipWork.setBackgroundResource(if (selectedWork) R.drawable.bg_day_work else R.drawable.bg_card)
        binding.chipOff.setBackgroundResource(if (!selectedWork) R.drawable.bg_day_off else R.drawable.bg_card)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}