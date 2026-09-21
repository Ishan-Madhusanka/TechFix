package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServiceDetailsActivity : AppCompatActivity() {

    private lateinit var tvServiceName: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var btnBookRepair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_service_details)

        tvServiceName = findViewById(R.id.tvServiceName)
        tvServicePrice = findViewById(R.id.tvServicePrice)
        btnBookRepair = findViewById(R.id.btnBookRepair)

        // Get selected service
        val serviceName = intent.getStringExtra("serviceName")

        // Get selected device category
        val categoryName = intent.getStringExtra("categoryName")

        val serviceId = intent.getStringExtra("SERVICE_ID")

        if (serviceName != null) {
            tvServiceName.text = serviceName
        }

        // Set price according to service
        val servicePrice = when (serviceName) {

            "Screen Repair" -> 8000

            "Battery Replacement" -> 6000

            "Charging Port Repair" -> 2500

            "Software Repair" -> 5500

            else -> 5500
        }

        tvServicePrice.text = "Estimated Price: Rs. $servicePrice"

        // Book Repair button
        btnBookRepair.setOnClickListener {

            val intent = Intent(
                this,
                BookRepairActivity::class.java
            )

            // Send service name
            intent.putExtra(
                "SERVICE_ID",
                serviceId
            )

            intent.putExtra(
                "serviceName",
                serviceName
            )

            // Send device category
            intent.putExtra(
                "categoryName",
                categoryName
            )

            // Send service price
            intent.putExtra(
                "servicePrice",
                servicePrice
            )

            startActivity(intent)
        }
    }
}