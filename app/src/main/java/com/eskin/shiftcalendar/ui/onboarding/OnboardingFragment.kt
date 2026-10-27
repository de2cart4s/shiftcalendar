package com.eskin.shiftcalendar.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.eskin.shiftcalendar.R
import com.eskin.shiftcalendar.databinding.FragmentOnboardingBinding
import com.eskin.shiftcalendar.ui.calendar.CalendarFragment

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStart.setOnClickListener {
            requireContext().getSharedPreferences("shift_calendar", Context.MODE_PRIVATE)
                .edit().putBoolean("onboarding_shown", true).apply()
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragment_container, CalendarFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}