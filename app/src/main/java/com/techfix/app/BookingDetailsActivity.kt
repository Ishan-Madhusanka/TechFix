package com.techfix.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var tvCategory: TextView
    private lateinit var tvService: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDevice: TextView
    private lateinit var tvProblem: TextView
    private lateinit var tvAppointment: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_booking_details)

        tvCategory = findViewById(R.id.tvCategory)
        tvService = findViewById(R.id.tvService)
        tvPrice = findViewById(R.id.tvPrice)
        tvDevice = findViewById(R.id.tvDevice)
        tvProblem = findViewById(R.id.tvProblem)
        tvAppointment = findViewById(R.id.tvAppointment)
        tvStatus = findViewById(R.id.tvStatus)

        val categoryName =
            intent.getStringExtra("categoryName") ?: ""

        val serviceName =
            intent.getStringExtra("serviceName") ?: ""

        val price =
            intent.getIntExtra("price", 0)

        val deviceBrand =
            intent.getStringExtra("deviceBrand") ?: ""

        val deviceModel =
            intent.getStringExtra("deviceModel") ?: ""

        val description =
            intent.getStringExtra("description") ?: ""

        val appointmentDate =
            intent.getStringExtra("appointmentDate") ?: ""

        val status =
            intent.getStringExtra("status") ?: "PENDING"

        tvCategory.text = "Category: $categoryName"

        tvService.text = "Service: $serviceName"

        tvPrice.text = "Estimated Price: Rs. $price"

        tvDevice.text = "Device: $deviceBrand $deviceModel"

        tvProblem.text = "Problem: $description"

        tvAppointment.text =
            "Appointment Date: $appointmentDate"

        tvStatus.text = "Status: $status"
    }
}