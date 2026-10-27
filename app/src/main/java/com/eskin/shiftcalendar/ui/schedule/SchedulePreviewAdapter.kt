package com.eskin.shiftcalendar.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.eskin.shiftcalendar.R
import com.eskin.shiftcalendar.data.ScheduleConfig
import com.eskin.shiftcalendar.data.ScheduleMode
import com.eskin.shiftcalendar.databinding.ItemSchedulePreviewBinding

class SchedulePreviewAdapter(private val onClick: (ScheduleConfig) -> Unit) :
    RecyclerView.Adapter<SchedulePreviewAdapter.VH>() {

    private val items = mutableListOf<ScheduleConfig>()

    fun submit(new: List<ScheduleConfig>) {
        items.clear()
        items.addAll(new)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemSchedulePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemSchedulePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val config = items[position]
        with(holder.binding) {
            scheduleName.text = config.name
            scheduleDescription.text = config.description
            renderPattern(patternContainer, config)
            root.setOnClickListener { onClick(config) }
        }
    }

    private fun renderPattern(container: LinearLayout, config: ScheduleConfig) {
        container.removeAllViews()
        val density = container.context.resources.displayMetrics.density
        val dotSize = (24 * density).toInt()
        val dotMargin = (4 * density).toInt()

        val pattern: List<Int> = when (config.mode) {
            ScheduleMode.CYCLE -> config.pattern
            ScheduleMode.WEEKLY -> config.weekDays.map { if (it) 1 else 0 }
        }

        for (isWork in pattern) {
            val params = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                marginEnd = dotMargin
            }
            val dot = View(container.context).apply {
                layoutParams = params
                setBackgroundResource(
                    if (isWork == 1) R.drawable.bg_dot_blue else R.drawable.bg_dot_yellow
                )
            }
            container.addView(dot)
        }
    }
}