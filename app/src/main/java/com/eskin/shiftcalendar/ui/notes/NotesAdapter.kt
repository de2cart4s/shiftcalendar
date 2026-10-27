package com.eskin.shiftcalendar.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eskin.shiftcalendar.data.DateUtils
import com.eskin.shiftcalendar.databinding.ItemNoteBinding

data class NoteItem(val key: String, val text: String, val work: Boolean)

class NotesAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<NotesAdapter.VH>() {

    private val items = mutableListOf<NoteItem>()

    fun submit(new: List<NoteItem>) {
        items.clear(); items.addAll(new); notifyDataSetChanged()
    }

    inner class VH(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.binding) {
            noteDate.text = DateUtils.formatRu(item.key)
            noteText.text = item.text
            noteStatus.text = if (item.work) "рабочий" else "выходной"
            root.setOnClickListener { onClick(item.key) }
        }
    }
}