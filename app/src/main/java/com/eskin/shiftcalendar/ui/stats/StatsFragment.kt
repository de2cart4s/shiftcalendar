package com.eskin.shiftcalendar.ui.stats

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eskin.shiftcalendar.R
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.databinding.FragmentStatsBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.config.observe(viewLifecycleOwner) { render() }
        vm.overrides.observe(viewLifecycleOwner) { render() }
        render()
    }

    private fun render() {
        val year = vm.currentYear
        val month = vm.currentMonth
        binding.periodLabel.text = "${DateUtils.monthName(month)} $year · график ${vm.config.value?.name ?: "-"}"
        val s = vm.monthStats(year, month)
        binding.statHours.text = s.third.toString()
        binding.statWork.text = s.first.toString()
        binding.statOff.text = s.second.toString()
        renderBars(vm.hoursByWeekday(year, month))
    }

    private fun renderBars(hours: List<Int>) {
        binding.barsContainer.removeAllViews()
        val labels = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
        val max = (hours.maxOrNull() ?: 1).coerceAtLeast(1)
        val density = resources.displayMetrics.density
        for (i in 0..6) {
            val column = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
            val bar = View(requireContext()).apply {
                val h = (hours[i].toFloat() / max * 100 * density).toInt().coerceAtLeast((4 * density).toInt())
                layoutParams = LinearLayout.LayoutParams((28 * density).toInt(), h)
                setBackgroundResource(if (i < 5) R.drawable.bg_day_work else R.drawable.bg_day_off)
            }
            val label = TextView(requireContext()).apply {
                text = labels[i]
                textSize = 10f
                setTextColor(requireContext().getColor(R.color.text_muted))
                gravity = Gravity.CENTER
            }
            column.addView(bar)
            column.addView(label)
            binding.barsContainer.addView(column)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}