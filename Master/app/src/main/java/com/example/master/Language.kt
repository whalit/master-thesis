package com.example.master

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.util.*
import com.example.master.databinding.FragmentLanguageBinding

class Language : Fragment() {

    private lateinit var binding: FragmentLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for each language option
        binding.flagFrench.setOnClickListener {
            setLocale("fr")
        }

        binding.flagEnglish.setOnClickListener {
            setLocale("en")
        }

        binding.flagNederlands.setOnClickListener {
            setLocale("nl")
        }
    }

    // Function to change the locale based on the language code
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        // Optionally, refresh the fragment or activity to apply the new language
        requireActivity().recreate() // Recreate the activity to apply language changes
    }
}
