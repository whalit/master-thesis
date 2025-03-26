package com.example.master
import com.example.master.databinding.MainPageBinding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainPage : AppCompatActivity() {

    private lateinit var binding : MainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())

        binding.bottomNav.setOnItemSelectedListener {

            when(it.itemId){

                R.id.navigation_home -> replaceFragment(Home())
                R.id.navigation_success -> replaceFragment(Success())
                R.id.navigation_history -> replaceFragment(History())
                R.id.navigation_language -> replaceFragment(Language())


                else ->{

                }

            }

            true

        }


    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


    }
}