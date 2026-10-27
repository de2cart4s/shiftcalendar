package com.eskin.shiftcalendar.ui.holidays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.databinding.ItemHolidayBinding

class HolidaysAdapter(private val onRemove: (String) -> Unit) :
    RecyclerView.Adapter<HolidaysAdapter.VH>() {

    private val items = mutableListOf<String>()

    fun submit(new: List<String>) {
        items.clear(); items.addAll(new); notifyDataSetChanged()
    }

    inner class VH(val binding: ItemHolidayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemHolidayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val key = items[position]
        holder.binding.holidayDate.text = DateUtils.formatRu(key)
        holder.binding.btnRemove.setOnClickListener { onRemove(key) }
    }
}