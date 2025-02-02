package com.example.master

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Q3 : AppCompatActivity() {

    // Define the question as a property since it's constant
    private val question = "are you comfortable with the accessibility of public spaces?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.q3)

        // Get the buttons
        val bQ31= findViewById<Button>(R.id.bQ31)
        val bQ32 = findViewById<Button>(R.id.bQ32)
        val bQ33 = findViewById<Button>(R.id.bQ33)

        // Set up button click listeners using a common function
        setupButtonClickListener(bQ31)
        setupButtonClickListener(bQ32)
        setupButtonClickListener(bQ33)
    }

    // Function to set up button click listeners in a common way
    private fun setupButtonClickListener(button: Button) {
        button.setOnClickListener {
            saveResponse(button.text.toString())
            val intent = Intent(this, Q4::class.java)
            startActivity(intent)
        }
    }

    // Function to save the response using JsonHelper
    private fun saveResponse(answer: String) {
        val response = Response(question, answer)
        JsonHelper.saveResponseToJson(response, this)  // Save to the JSON file
    }
}
