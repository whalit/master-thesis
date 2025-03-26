package com.example.master

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val button: Button = findViewById(R.id.btn1)
        button.setOnClickListener {
            // Start the Q1 activity
            val intent = Intent(this, Q1::class.java)
            startActivity(intent)
        }
    }
}
