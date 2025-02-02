package com.example.master

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Q4 : AppCompatActivity() {

    // Define the question as a property since it's constant
    private val question = "How independent do you feel in performing daily activities?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.q4)

        // Get the buttons
        val bQ41= findViewById<Button>(R.id.bQ41)
        val bQ42 = findViewById<Button>(R.id.bQ42)
        val bQ43 = findViewById<Button>(R.id.bQ43)

        // Set up button click listeners using a common function
        setupButtonClickListener(bQ41)
        setupButtonClickListener(bQ42)
        setupButtonClickListener(bQ43)
    }

    // Function to set up button click listeners in a common way
    private fun setupButtonClickListener(button: Button) {
        button.setOnClickListener {
            saveResponse(button.text.toString())
            setContentView(R.layout.main_page)
        }
    }

    // Function to save the response using JsonHelper
    private fun saveResponse(answer: String) {
        val response = Response(question, answer)
        JsonHelper.saveResponseToJson(response, this)  // Save to the JSON file
    }
}
