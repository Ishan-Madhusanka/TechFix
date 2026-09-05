package com.techfix.app.branch

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.techfix.app.R

class BookingConfirmationActivity : AppCompatActivity() {

    private var branchId: String? = null
    private var branchName: String? = null
    private var serviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_booking_confirmation)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Get booking data
        branchId = intent.getStringExtra("BRANCH_ID")
        branchName = intent.getStringExtra("BRANCH_NAME")
        serviceId = intent.getStringExtra("SERVICE_ID")

        // UI
        val tvSelectedService =
            findViewById<TextView>(R.id.tvSelectedService)

        val tvAssignedBranch =
            findViewById<TextView>(R.id.tvAssignedBranch)

        val tvBranchId =
            findViewById<TextView>(R.id.tvBranchId)

        val btnConfirmBooking =
            findViewById<MaterialButton>(R.id.btnConfirmBooking)

        val btnCancelBooking =
            findViewById<MaterialButton>(R.id.btnCancelBooking)

        // Display received data
        tvSelectedService.text =
            "Service ID: ${serviceId ?: "--"}"

        tvAssignedBranch.text =
            "Branch: ${branchName ?: "--"}"

        tvBranchId.text =
            "Branch ID: ${branchId ?: "--"}"

        // Confirm button
        btnConfirmBooking.setOnClickListener {
            // Actual repair request creation will be integrated
            // with the booking flow later.
        }

        // Cancel
        btnCancelBooking.setOnClickListener {
            finish()
        }
    }
}