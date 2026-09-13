package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RepairServicesActivity : AppCompatActivity() {

    private lateinit var btnScreenRepair: Button
    private lateinit var btnBatteryRepair: Button
    private lateinit var btnChargingRepair: Button
    private lateinit var btnSoftwareRepair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_repair_services)

        btnScreenRepair = findViewById(R.id.btnScreenRepair)
        btnBatteryRepair = findViewById(R.id.btnBatteryRepair)
        btnChargingRepair = findViewById(R.id.btnChargingRepair)
        btnSoftwareRepair = findViewById(R.id.btnSoftwareRepair)

        btnScreenRepair.setOnClickListener {
            openServiceDetails("Screen Repair")
        }

        btnBatteryRepair.setOnClickListener {
            openServiceDetails("Battery Replacement")
        }

        btnChargingRepair.setOnClickListener {
            openServiceDetails("Charging Port Repair")
        }

        btnSoftwareRepair.setOnClickListener {
            openServiceDetails("Software Repair")
        }
    }

    private fun openServiceDetails(serviceName: String) {

        val intent = Intent(this, ServiceDetailsActivity::class.java)

        intent.putExtra("serviceName", serviceName)

        startActivity(intent)
    }
}