package com.example.master

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Q1 : AppCompatActivity() {

    // Define the question as a property since it's constant
    private val question = "Is this your first time using a wheelchair?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.q1)

        // Get the buttons
        val btnYes1 = findViewById<Button>(R.id.btnYes1)
        val btnYes2 = findViewById<Button>(R.id.btnYes2)
        val btnNo = findViewById<Button>(R.id.btnNo)

        // Set up button click listeners using a common function
        setupButtonClickListener(btnYes1)
        setupButtonClickListener(btnYes2)
        setupButtonClickListener(btnNo)
    }

    // Function to set up button click listeners in a common way
    private fun setupButtonClickListener(button: Button) {
        button.setOnClickListener {
            saveResponse(button.text.toString())
            val intent = Intent(this, Q2::class.java)
            startActivity(intent)
        }
    }

    // Function to save the response using JsonHelper
    private fun saveResponse(answer: String) {
        val response = Response(question, answer)
        JsonHelper.saveResponseToJson(response, this)  // Save to the JSON file
    }
}
