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
            openServiceDetails("Screen Repair", categoryName)
        }

        // Battery Replacement
        btnBatteryRepair.setOnClickListener {
            openServiceDetails("Battery Replacement", categoryName)
        }

        // Charging Port Repair
        btnChargingRepair.setOnClickListener {
            openServiceDetails("Charging Port Repair", categoryName)
        }

        // Software Repair
        btnSoftwareRepair.setOnClickListener {
            openServiceDetails("Software Repair", categoryName)
        }
    }

    private fun openServiceDetails(
        serviceName: String,
        categoryName: String?
    ) {

        val intent = Intent(
            this,
            ServiceDetailsActivity::class.java
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