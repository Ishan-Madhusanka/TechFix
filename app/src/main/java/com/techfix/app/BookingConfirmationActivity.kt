package com.techfix.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var tvConfirmationService: TextView
    private lateinit var tvConfirmationDevice: TextView
    private lateinit var tvConfirmationDate: TextView
    private lateinit var tvConfirmationStatus: TextView
    private lateinit var btnBackToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_booking_confirmation)

        tvConfirmationService = findViewById(R.id.tvConfirmationService)
        tvConfirmationDevice = findViewById(R.id.tvConfirmationDevice)
        tvConfirmationDate = findViewById(R.id.tvConfirmationDate)
        tvConfirmationStatus = findViewById(R.id.tvConfirmationStatus)
        btnBackToHome = findViewById(R.id.btnBackToHome)

        // Get booking details
        val serviceName = intent.getStringExtra("serviceName")
        val deviceBrand = intent.getStringExtra("deviceBrand")
        val deviceModel = intent.getStringExtra("deviceModel")
        val appointmentDate = intent.getStringExtra("appointmentDate")

        // Display booking details
        tvConfirmationService.text =
            "Service: ${serviceName ?: "Unknown Service"}"

        tvConfirmationDevice.text =
            "Device: ${deviceBrand ?: ""} ${deviceModel ?: ""}"

        tvConfirmationDate.text =
            "Appointment Date: ${appointmentDate ?: ""}"

        tvConfirmationStatus.text =
            "Status: PENDING"

        // Back to Home
        btnBackToHome.setOnClickListener {
            finish()
        }
    }
}