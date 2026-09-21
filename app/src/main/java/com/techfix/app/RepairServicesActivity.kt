package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RepairServicesActivity : AppCompatActivity() {

    private lateinit var tvCategoryName: TextView

    private lateinit var btnScreenRepair: Button
    private lateinit var btnBatteryRepair: Button
    private lateinit var btnChargingRepair: Button
    private lateinit var btnSoftwareRepair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_repair_services)

        tvCategoryName = findViewById(R.id.tvCategoryName)

        btnScreenRepair = findViewById(R.id.btnScreenRepair)
        btnBatteryRepair = findViewById(R.id.btnBatteryRepair)
        btnChargingRepair = findViewById(R.id.btnChargingRepair)
        btnSoftwareRepair = findViewById(R.id.btnSoftwareRepair)

        // Get selected device category
        val categoryName = intent.getStringExtra("categoryName")

        if (categoryName != null) {
            tvCategoryName.text = "Services for: $categoryName"
        }

        // Screen Repair
        btnScreenRepair.setOnClickListener {

            val serviceId = when (categoryName) {
                "Laptop" -> "laptop_screen_repair"
                "Mobile" -> "mobile_screen_repair"
                else -> "laptop_screen_repair"
            }

            openServiceDetails(
                serviceId,
                "Screen Repair",
                categoryName
            )
        }

        // Battery Replacement
        btnBatteryRepair.setOnClickListener {
            openServiceDetails(
                "",
                "Battery Replacement",
                categoryName
            )
        }

        // Charging Port Repair
        btnChargingRepair.setOnClickListener {
            openServiceDetails(
                "",
                "Charging Port Repair",
                categoryName
            )
        }

        // Software Repair
        btnSoftwareRepair.setOnClickListener {

            val serviceId = when (categoryName) {
                "Desktop" -> "desktop_os_install"
                else -> ""
            }

            openServiceDetails(
                serviceId,
                "Software Repair",
                categoryName
            )
        }
    }

    private fun openServiceDetails(
        serviceId: String,
        serviceName: String,
        categoryName: String?
    ) {

        val intent = Intent(
            this,
            ServiceDetailsActivity::class.java
        )

        // Firestore service document ID
        intent.putExtra(
            "SERVICE_ID",
            serviceId
        )

        intent.putExtra(
            "serviceName",
            serviceName
        )

        intent.putExtra(
            "categoryName",
            categoryName
        )

        startActivity(intent)
    }
}