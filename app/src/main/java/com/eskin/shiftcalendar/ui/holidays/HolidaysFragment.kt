package com.eskin.shiftcalendar.ui.holidays

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.databinding.FragmentHolidaysBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import java.util.Calendar

class HolidaysFragment : Fragment() {

    private var _binding: FragmentHolidaysBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()
    private lateinit var adapter: HolidaysAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHolidaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HolidaysAdapter { key -> vm.removeHoliday(key) }
        binding.holidaysList.layoutManager = LinearLayoutManager(requireContext())
        binding.holidaysList.adapter = adapter

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnAddHoliday.setOnClickListener {
            val today = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, y, m, d -> vm.addHoliday(DateUtils.keyOf(y, m, d)) },
                today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        vm.holidays.observe(viewLifecycleOwner) { set ->
            adapter.submit(set.sorted())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}