package com.techfix.app.branch

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.RepairRequest
import com.techfix.app.repository.RepairRequestRepository

class BookingConfirmationActivity : AppCompatActivity() {

    private var branchId: String? = null
    private var branchName: String? = null
    private var serviceId: String? = null

    private var technicianId: String? = null
    private var technicianName: String? = null

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

        technicianId = intent.getStringExtra("TECHNICIAN_ID")
        technicianName = intent.getStringExtra("TECHNICIAN_NAME")

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

        // Confirm Booking
        btnConfirmBooking.setOnClickListener {

            val currentBranchId = branchId
            val currentServiceId = serviceId
            val currentTechnicianId = technicianId

            if (
                currentBranchId.isNullOrBlank() ||
                currentServiceId.isNullOrBlank() ||
                currentTechnicianId.isNullOrBlank()
            ) {

                Toast.makeText(
                    this,
                    "Booking information is incomplete",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            btnConfirmBooking.isEnabled = false
            btnConfirmBooking.text = "Confirming..."

            val repairRequest = RepairRequest(
                serviceId = currentServiceId,
                branchId = currentBranchId,
                technicianId = currentTechnicianId,
                status = "Pending"
            )

            val repairRequestRepository =
                RepairRequestRepository()

            repairRequestRepository.createRepairRequest(

                repairRequest = repairRequest,

                onSuccess = { requestId ->

                    Toast.makeText(
                        this,
                        "Booking confirmed successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    btnConfirmBooking.text = "Booking Confirmed"

                    android.util.Log.d(
                        "TECHFIX_BOOKING",
                        "Repair Request Created: $requestId"
                    )
                },

                onFailure = { exception ->

                    btnConfirmBooking.isEnabled = true
                    btnConfirmBooking.text = "Confirm Booking"

                    Toast.makeText(
                        this,
                        "Booking failed: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    android.util.Log.e(
                        "TECHFIX_BOOKING",
                        "Booking error: ${exception.message}"
                    )
                }
            )
        }

        // Cancel
        btnCancelBooking.setOnClickListener {
            finish()
        }
    }
}