package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var tvConfirmationService: TextView
    private lateinit var tvConfirmationPrice: TextView
    private lateinit var tvConfirmationDevice: TextView
    private lateinit var tvConfirmationDate: TextView
    private lateinit var tvConfirmationStatus: TextView
    private lateinit var btnBackToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_booking_confirmation)

        tvConfirmationService =
            findViewById(R.id.tvConfirmationService)

        tvConfirmationPrice =
            findViewById(R.id.tvConfirmationPrice)

        tvConfirmationDevice =
            findViewById(R.id.tvConfirmationDevice)

        tvConfirmationDate =
            findViewById(R.id.tvConfirmationDate)

        tvConfirmationStatus =
            findViewById(R.id.tvConfirmationStatus)

        btnBackToHome =
            findViewById(R.id.btnBackToHome)

        val categoryName =
            intent.getStringExtra("categoryName")

        val serviceName =
            intent.getStringExtra("serviceName")

        val servicePrice =
            intent.getIntExtra("servicePrice", 0)

        val deviceBrand =
            intent.getStringExtra("deviceBrand")

        val deviceModel =
            intent.getStringExtra("deviceModel")

        val appointmentDate =
            intent.getStringExtra("appointmentDate")

        tvConfirmationService.text =
            "Category: ${categoryName ?: "Unknown Category"}\n" +
                    "Service: ${serviceName ?: "Unknown Service"}"

        tvConfirmationPrice.text =
            "Estimated Price: Rs. $servicePrice"

        tvConfirmationDevice.text =
            "Device: ${deviceBrand ?: ""} ${deviceModel ?: ""}"

        tvConfirmationDate.text =
            "Appointment Date: ${appointmentDate ?: ""}"

        tvConfirmationStatus.text =
            "Status: PENDING"

        // Back to Home
        btnBackToHome.setOnClickListener {

            val homeIntent = Intent(
                this,
                HomeActivity::class.java
            )

            homeIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(homeIntent)

            finish()
        }
    }
}