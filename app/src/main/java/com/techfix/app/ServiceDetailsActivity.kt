package com.techfix.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ServiceDetailsActivity : AppCompatActivity() {

    private lateinit var tvServiceName: TextView
    private lateinit var btnBookRepair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_service_details)

        tvServiceName = findViewById(R.id.tvServiceName)
        btnBookRepair = findViewById(R.id.btnBookRepair)

        // Get selected service name
        val serviceName = intent.getStringExtra("serviceName")

        if (serviceName != null) {
            tvServiceName.text = serviceName
        }

        btnBookRepair.setOnClickListener {

            Toast.makeText(
                this,
                "Booking screen coming next!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}