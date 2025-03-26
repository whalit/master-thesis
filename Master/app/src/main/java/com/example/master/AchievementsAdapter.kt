package com.example.master

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.master.databinding.AchievementBinding

class AchievementsAdapter(private var achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = AchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.bind(achievement)
    }

    override fun getItemCount(): Int = achievements.size

    // Method to update the list of achievements
    fun updateAchievements(newAchievements: List<Achievement>) {
        this.achievements = newAchievements
        notifyDataSetChanged()  // Notify the adapter that the data set has changed
    }

    inner class AchievementViewHolder(private val binding: AchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.achievementIcon.setImageResource(achievement.icon)
            binding.achievementTitle.text = achievement.title
        }
    }
}
