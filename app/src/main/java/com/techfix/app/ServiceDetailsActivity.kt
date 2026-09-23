package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServiceDetailsActivity : AppCompatActivity() {

    private lateinit var tvServiceName: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var tvServiceDuration: TextView
    private lateinit var btnBookRepair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_service_details)

        // Connect XML views
        tvServiceName = findViewById(R.id.tvServiceName)
        tvServicePrice = findViewById(R.id.tvServicePrice)
        tvServiceDuration = findViewById(R.id.tvServiceDuration)
        btnBookRepair = findViewById(R.id.btnBookRepair)

        // Get service data from RepairServicesActivity
        val serviceId =
            intent.getStringExtra("serviceId")

        val serviceName =
            intent.getStringExtra("serviceName")

        val categoryName =
            intent.getStringExtra("categoryName")

        val servicePrice =
            intent.getIntExtra("servicePrice", 0)

        val duration =
            intent.getStringExtra("duration")

        // Display service name
        tvServiceName.text =
            serviceName ?: "Unknown Service"

        // Display Firebase price
        tvServicePrice.text =
            "Estimated Price: Rs. $servicePrice"

        // Display service duration
        tvServiceDuration.text =
            "Duration: ${duration ?: "Not specified"}"

        // Book Repair button
        btnBookRepair.setOnClickListener {

            val bookingIntent = Intent(
                this,
                BookRepairActivity::class.java
            )

            // Send service ID
            bookingIntent.putExtra(
                "serviceId",
                serviceId
            )

            // Send service name
            bookingIntent.putExtra(
                "serviceName",
                serviceName
            )

            // Send device category
            bookingIntent.putExtra(
                "categoryName",
                categoryName
            )

            // Send Firebase price
            bookingIntent.putExtra(
                "servicePrice",
                servicePrice
            )

            // Send duration
            bookingIntent.putExtra(
                "duration",
                duration
            )

            startActivity(bookingIntent)
        }
    }
}