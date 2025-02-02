package com.example.master

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Q2 : AppCompatActivity() {

    // Define the question as a property since it's constant
    private val question = "Do you need assistance in your daily life?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.q2)

        // Get the buttons
        val btn1= findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)

        // Set up button click listeners using a common function
        setupButtonClickListener(btn1)
        setupButtonClickListener(btn2)
        setupButtonClickListener(btn3)
    }

    // Function to set up button click listeners in a common way
    private fun setupButtonClickListener(button: Button) {
        button.setOnClickListener {
            saveResponse(button.text.toString())
            val intent = Intent(this, Q3::class.java)
            startActivity(intent)
        }
    }

    // Function to save the response using JsonHelper
    private fun saveResponse(answer: String) {
        val response = Response(question, answer)
        JsonHelper.saveResponseToJson(response, this)  // Save to the JSON file
    }
}
