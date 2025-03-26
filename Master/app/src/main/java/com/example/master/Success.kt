package com.example.master

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.master.databinding.FragmentSuccessBinding
import com.google.android.material.button.MaterialButton

class Success : Fragment() {

    private lateinit var binding: FragmentSuccessBinding
    private lateinit var achievementsAdapter: AchievementsAdapter
    private var achievements: List<Achievement> = listOf()
    private var isDoneFilterActive = false
    private var isUndoneFilterActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the list of achievements
        achievements = listOf(
            Achievement(R.drawable.unl, "First Use Of The App", true),
            Achievement(R.drawable.unl, "05 Km Traveled", true),
            Achievement(R.drawable.unl, "10 Km Traveled", true),
            Achievement(R.drawable.lock, "15 Km Traveled", false),
            Achievement(R.drawable.unl, "20 Km Traveled", true),
            Achievement(R.drawable.lock, "Move On The 03 Types Of Roads", false),
            Achievement(R.drawable.lock, "Speed Runner", false),
            Achievement(R.drawable.unl, "Unlock 05 successes", true),
            Achievement(R.drawable.lock, "Unlock 10 successes", false),
            Achievement(R.drawable.unl, "Change The Language", true),
            Achievement(R.drawable.lock, "See All Sections Of The Help Page", false),
        )

        // Set up the RecyclerView
        achievementsAdapter = AchievementsAdapter(achievements)
        binding.achievementsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.achievementsRecyclerView.adapter = achievementsAdapter

        // Set up filter buttons
        binding.btnDone.setOnClickListener {
            toggleFilter(true) // Filter done achievements
        }

        binding.btnUndone.setOnClickListener {
            toggleFilter(false) // Filter undone achievements
        }
    }

    private fun toggleFilter(isDone: Boolean) {
        if (isDone) {
            if (isDoneFilterActive) {
                // If the filter is already active, deactivate it and show all achievements
                resetFilter()
                isDoneFilterActive = false
                changeButtonColor(binding.btnDone, false)
            } else {
                // Activate the filter to show only done achievements
                filterAchievements(true)
                isDoneFilterActive = true
                isUndoneFilterActive = false
                changeButtonColor(binding.btnDone, true)
                changeButtonColor(binding.btnUndone, false)
            }
        } else {
            if (isUndoneFilterActive) {
                // If the filter is already active, deactivate it and show all achievements
                resetFilter()
                isUndoneFilterActive = false
                changeButtonColor(binding.btnUndone, false)
            } else {
                // Activate the filter to show only undone achievements
                filterAchievements(false)
                isUndoneFilterActive = true
                isDoneFilterActive = false
                changeButtonColor(binding.btnUndone, true)
                changeButtonColor(binding.btnDone, false)
            }
        }
    }

    private fun filterAchievements(showDone: Boolean) {
        val filteredAchievements = achievements.filter { it.status == showDone }
        achievementsAdapter.updateAchievements(filteredAchievements)
    }

    private fun resetFilter() {
        achievementsAdapter.updateAchievements(achievements)
    }

    private fun changeButtonColor(button: MaterialButton, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy))
            button.setTextColor(Color.WHITE)
        } else {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.navy))
        }
    }
}
