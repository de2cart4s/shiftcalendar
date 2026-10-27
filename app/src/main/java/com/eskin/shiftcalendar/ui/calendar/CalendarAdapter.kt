package com.eskin.shiftcalendar.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eskin.shiftcalendar.R
import com.eskin.shiftcalendar.data.DayCell
import com.eskin.shiftcalendar.databinding.ItemDayCellBinding

class CalendarAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.VH>() {

    private val cells = mutableListOf<DayCell>()

    fun submit(new: List<DayCell>) {
        cells.clear(); cells.addAll(new); notifyDataSetChanged()
    }

    inner class VH(val binding: ItemDayCellBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemDayCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = cells.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cell = cells[position]
        with(holder.binding) {
            if (cell.isEmpty) {
                dayText.text = ""
                root.setBackgroundResource(0)
                root.setOnClickListener(null)
            } else {
                dayText.text = cell.day.toString()
                root.setBackgroundResource(
                    when {
                        cell.isToday -> R.drawable.bg_day_today
                        cell.work -> R.drawable.bg_day_work
                        else -> R.drawable.bg_day_off
                    }
                )
                dayText.setTextColor(
                    root.context.getColor(if (cell.isToday) R.color.text_dark else if (cell.work) R.color.white else R.color.text_dark)
                )
                root.setOnClickListener { onClick(cell.key) }
            }
        }
    }
}