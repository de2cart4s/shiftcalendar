package com.eskin.shiftcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.eskin.shiftcalendar.databinding.ActivityMainBinding
import com.eskin.shiftcalendar.ui.calendar.CalendarFragment
import com.eskin.shiftcalendar.ui.settings.SettingsFragment
import com.eskin.shiftcalendar.ui.stats.StatsFragment
import com.eskin.shiftcalendar.ui.onboarding.OnboardingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            binding.bottomNav.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val prefs = getSharedPreferences("shift_calendar", MODE_PRIVATE)
            val onboardingShown = prefs.getBoolean("onboarding_shown", false)
            openTab(if (onboardingShown) CalendarFragment() else OnboardingFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendar -> openTab(CalendarFragment())
                R.id.nav_stats -> openTab(StatsFragment())
                R.id.nav_settings -> openTab(SettingsFragment())
            }
            true
        }
    }

    private fun openTab(fragment: Fragment) {
        supportFragmentManager.commit { replace(R.id.fragment_container, fragment) }
    }
}

fun Fragment.openScreen(fragment: Fragment) {
    requireActivity().supportFragmentManager.commit {
        replace(R.id.fragment_container, fragment)
        addToBackStack(null)
    }
}
