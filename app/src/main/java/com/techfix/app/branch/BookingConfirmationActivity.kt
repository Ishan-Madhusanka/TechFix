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

    // ---------------------------------------------------------
    // MEMBER 1 DATA
    // ---------------------------------------------------------

    private var branchId: String? = null
    private var branchName: String? = null
    private var serviceId: String? = null

    private var technicianId: String? = null
    private var technicianName: String? = null

    // ---------------------------------------------------------
    // MEMBER 2 BOOKING DATA
    // ---------------------------------------------------------

    private var customerId: String = ""
    private var customerEmail: String = ""
    private var categoryName: String = ""
    private var serviceName: String = ""
    private var deviceBrand: String = ""
    private var deviceModel: String = ""
    private var description: String = ""
    private var appointmentDate: String = ""
    private var imageUri: String = ""
    private var servicePrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_booking_confirmation)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // ---------------------------------------------------------
        // RECEIVE MEMBER 1 SUITABLE BRANCH DATA
        // ---------------------------------------------------------

        branchId =
            intent.getStringExtra("BRANCH_ID")

        branchName =
            intent.getStringExtra("BRANCH_NAME")

        serviceId =
            intent.getStringExtra("SERVICE_ID")

        technicianId =
            intent.getStringExtra("TECHNICIAN_ID")

        technicianName =
            intent.getStringExtra("TECHNICIAN_NAME")

        // ---------------------------------------------------------
        // READ MEMBER 2 BOOKING DATA
        // ---------------------------------------------------------

        val bookingPreferences =
            getSharedPreferences(
                "TECHFIX_BOOKING_DATA",
                MODE_PRIVATE
            )

        customerId =
            bookingPreferences.getString(
                "customerId",
                ""
            ) ?: ""

        customerEmail =
            bookingPreferences.getString(
                "customerEmail",
                ""
            ) ?: ""

        categoryName =
            bookingPreferences.getString(
                "categoryName",
                ""
            ) ?: ""

        serviceName =
            bookingPreferences.getString(
                "serviceName",
                ""
            ) ?: ""

        deviceBrand =
            bookingPreferences.getString(
                "deviceBrand",
                ""
            ) ?: ""

        deviceModel =
            bookingPreferences.getString(
                "deviceModel",
                ""
            ) ?: ""

        description =
            bookingPreferences.getString(
                "description",
                ""
            ) ?: ""

        appointmentDate =
            bookingPreferences.getString(
                "appointmentDate",
                ""
            ) ?: ""

        imageUri =
            bookingPreferences.getString(
                "imageUri",
                ""
            ) ?: ""

        servicePrice =
            bookingPreferences.getInt(
                "servicePrice",
                0
            )

        // ---------------------------------------------------------
        // CONNECT UI
        // ---------------------------------------------------------

        val tvSelectedService =
            findViewById<TextView>(
                R.id.tvSelectedService
            )

        val tvAssignedBranch =
            findViewById<TextView>(
                R.id.tvAssignedBranch
            )

        val tvBranchId =
            findViewById<TextView>(
                R.id.tvBranchId
            )

        val btnConfirmBooking =
            findViewById<MaterialButton>(
                R.id.btnConfirmBooking
            )

        val btnCancelBooking =
            findViewById<MaterialButton>(
                R.id.btnCancelBooking
            )

        // ---------------------------------------------------------
        // DISPLAY SELECTED DATA
        // ---------------------------------------------------------

        tvSelectedService.text =
            if (serviceName.isNotBlank()) {

                "Service: $serviceName"

            } else {

                "Service ID: ${serviceId ?: "--"}"
            }

        tvAssignedBranch.text =
            "Branch: ${branchName ?: "--"}"

        tvBranchId.text =
            "Branch ID: ${branchId ?: "--"}"

        // ---------------------------------------------------------
        // CONFIRM BOOKING
        // ---------------------------------------------------------

        btnConfirmBooking.setOnClickListener {

            val currentBranchId =
                branchId

            val currentServiceId =
                serviceId

            val currentTechnicianId =
                technicianId

            // Validate Member 1 integration data
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

            // Validate Member 2 customer
            if (customerId.isBlank()) {

                Toast.makeText(
                    this,
                    "Customer information is missing",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            btnConfirmBooking.isEnabled = false
            btnConfirmBooking.text = "Confirming..."

            // -----------------------------------------------------
            // CREATE FINAL REPAIR REQUEST
            // -----------------------------------------------------

            val repairRequest =
                RepairRequest(

                    // Member 2 customer data
                    customerId = customerId,

                    // Member 2 device data
                    categoryId = categoryName,
                    deviceBrand = deviceBrand,
                    deviceModel = deviceModel,

                    // Member 1 selected service
                    serviceId = currentServiceId,

                    // Member 2 problem details
                    description = description,

                    // Member 2 selected image
                    imageUrl = imageUri,

                    // Member 1 suitable branch
                    branchId = currentBranchId,

                    // Member 1 available technician
                    technicianId = currentTechnicianId,

                    // Member 2 appointment
                    appointmentDate = appointmentDate,

                    // Initial repair status
                    status = "Pending",

                    // Member 2 service price
                    price = servicePrice.toDouble()
                )

            // -----------------------------------------------------
            // SAVE USING MEMBER 1 REPOSITORY
            // -----------------------------------------------------

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

                    btnConfirmBooking.text =
                        "Booking Confirmed"

                    android.util.Log.d(
                        "TECHFIX_BOOKING",
                        "Repair Request Created: $requestId"
                    )

                    // Booking is completed.
                    // Remove temporary Member 2 data.
                    bookingPreferences
                        .edit()
                        .clear()
                        .apply()
                },

                onFailure = { exception ->

                    btnConfirmBooking.isEnabled = true

                    btnConfirmBooking.text =
                        "Confirm Booking"

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

        // ---------------------------------------------------------
        // CANCEL BOOKING
        // ---------------------------------------------------------

        btnCancelBooking.setOnClickListener {

            finish()
        }
    }
}