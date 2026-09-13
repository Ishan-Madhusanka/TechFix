package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

        // Book Repair button
        btnBookRepair.setOnClickListener {

            val intent = Intent(this, BookRepairActivity::class.java)

            intent.putExtra("serviceName", serviceName)

            startActivity(intent)
        }
    }
}