package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var btnCategories: Button
    private lateinit var btnServices: Button
    private lateinit var btnProfile: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        btnCategories = findViewById(R.id.btnCategories)
        btnServices = findViewById(R.id.btnServices)
        btnProfile = findViewById(R.id.btnProfile)
        btnLogout = findViewById(R.id.btnLogout)

        // Device Categories
        btnCategories.setOnClickListener {
            val intent = Intent(this, DeviceCategoriesActivity::class.java)
            startActivity(intent)
        }

        // Repair Services
        btnServices.setOnClickListener {
            Toast.makeText(
                this,
                "Repair Services coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // My Profile
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Logout
        btnLogout.setOnClickListener {
            finish()
        }
    }
}