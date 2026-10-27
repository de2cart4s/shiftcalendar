package com.eskin.shiftcalendar.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eskin.shiftcalendar.databinding.FragmentNotesListBinding
import com.eskin.shiftcalendar.ui.ShiftViewModel
import com.eskin.shiftcalendar.ui.day.DayDetailsSheet

class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val vm: ShiftViewModel by activityViewModels()
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NotesAdapter { key ->
            DayDetailsSheet.newInstance(key).show(childFragmentManager, "day_details")
        }
        binding.notesList.layoutManager = LinearLayoutManager(requireContext())
        binding.notesList.adapter = adapter

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        vm.notes.observe(viewLifecycleOwner) { render() }
        vm.overrides.observe(viewLifecycleOwner) { render() }
        vm.config.observe(viewLifecycleOwner) { render() }
        render()
    }

    private fun render() {
        val notes = vm.notes.value.orEmpty()
        val list = notes.entries
            .sortedByDescending { it.key }
            .map { NoteItem(it.key, it.value, vm.isWorkDay(it.key)) }
        adapter.submit(list)
        binding.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.notesList.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}