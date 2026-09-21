package com.techfix.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var tvNoBookings: TextView
    private lateinit var bookingsContainer: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_bookings)

        tvNoBookings = findViewById(R.id.tvNoBookings)
        bookingsContainer = findViewById(R.id.bookingsContainer)

        loadMyBookings()
    }

    private fun loadMyBookings() {

        val currentUser = auth.currentUser

        // Check whether customer is logged in
        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please login first.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val customerId = currentUser.uid

        // Get bookings belonging to the current customer
        db.collection("repairRequests")
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { documents ->

                bookingsContainer.removeAllViews()

                if (documents.isEmpty) {

                    tvNoBookings.visibility = TextView.VISIBLE

                    return@addOnSuccessListener
                }

                tvNoBookings.visibility = TextView.GONE

                // Display newest bookings first
                val bookings = documents.documents.sortedByDescending {
                    it.getTimestamp("createdAt")
                }

                for (document in bookings) {

                    val categoryName =
                        document.getString("categoryName")
                            ?: "Unknown Category"

                    val serviceName =
                        document.getString("serviceName")
                            ?: "Unknown Service"

                    val servicePrice =
                        document.getLong("servicePrice")?.toInt() ?: 0

                    val deviceBrand =
                        document.getString("deviceBrand")
                            ?: ""

                    val deviceModel =
                        document.getString("deviceModel")
                            ?: ""

                    val appointmentDate =
                        document.getString("appointmentDate")
                            ?: ""

                    val status =
                        document.getString("status")
                            ?: "PENDING"

                    addBookingCard(
                        categoryName,
                        serviceName,
                        servicePrice,
                        deviceBrand,
                        deviceModel,
                        appointmentDate,
                        status
                    )
                }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Failed to load bookings: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun addBookingCard(
        categoryName: String,
        serviceName: String,
        servicePrice: Int,
        deviceBrand: String,
        deviceModel: String,
        appointmentDate: String,
        status: String
    ) {

        val bookingLayout = LinearLayout(this)

        bookingLayout.orientation = LinearLayout.VERTICAL

        bookingLayout.setPadding(
            20,
            20,
            20,
            20
        )

        bookingLayout.setBackgroundResource(
            android.R.drawable.dialog_holo_light_frame
        )

        val categoryText = TextView(this)

        categoryText.text =
            "Category: $categoryName"

        categoryText.textSize = 18f

        categoryText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val serviceText = TextView(this)

        serviceText.text =
            "Service: $serviceName"

        serviceText.textSize = 17f

        val priceText = TextView(this)

        priceText.text =
            "Estimated Price: Rs. $servicePrice"

        priceText.textSize = 17f

        priceText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val deviceText = TextView(this)

        deviceText.text =
            "Device: $deviceBrand $deviceModel"

        deviceText.textSize = 16f

        val dateText = TextView(this)

        dateText.text =
            "Appointment Date: $appointmentDate"

        dateText.textSize = 16f

        val statusText = TextView(this)

        statusText.text =
            "Status: $status"

        statusText.textSize = 17f

        statusText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        bookingLayout.addView(categoryText)
        bookingLayout.addView(serviceText)
        bookingLayout.addView(priceText)
        bookingLayout.addView(deviceText)
        bookingLayout.addView(dateText)
        bookingLayout.addView(statusText)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            0,
            0,
            20
        )

        bookingsContainer.addView(
            bookingLayout,
            params
        )
    }
}