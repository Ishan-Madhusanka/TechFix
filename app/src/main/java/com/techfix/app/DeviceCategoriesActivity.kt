package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class DeviceCategoriesActivity : AppCompatActivity() {

    private lateinit var btnMobile: View
    private lateinit var btnLaptop: View
    private lateinit var btnDesktop: View
    private lateinit var btnGaming: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_device_categories)

        btnMobile = findViewById(R.id.btnMobile)
        btnLaptop = findViewById(R.id.btnLaptop)
        btnDesktop = findViewById(R.id.btnDesktop)
        btnGaming = findViewById(R.id.btnGaming)

        // Mobile
        btnMobile.setOnClickListener {
            openRepairServices("Mobile")
        }

        // Laptop
        btnLaptop.setOnClickListener {
            openRepairServices("Laptop")
        }

        // Desktop
        btnDesktop.setOnClickListener {
            openRepairServices("Desktop")
        }

        // Gaming Console
        btnGaming.setOnClickListener {
            openRepairServices("Gaming Console")
        }
    }

    private fun openRepairServices(categoryName: String) {

        val intent = Intent(
            this,
            RepairServicesActivity::class.java
        )

        intent.putExtra(
            "categoryName",
            categoryName
        )

        startActivity(intent)
    }
}