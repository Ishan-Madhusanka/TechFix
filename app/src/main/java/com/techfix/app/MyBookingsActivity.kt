package com.techfix.app

import android.graphics.Color
import android.graphics.Typeface
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

                // Newest bookings first
                val bookings =
                    documents.documents.sortedByDescending {
                        it.getTimestamp("createdAt")
                    }

                for (document in bookings) {

                    val categoryId =
                        document.getString("categoryId")
                            ?: ""

                    val serviceId =
                        document.getString("serviceId")
                            ?: ""

                    val price =
                        document.getDouble("price")?.toInt()
                            ?: document.getLong("price")?.toInt()
                            ?: 0

                    val deviceBrand =
                        document.getString("deviceBrand")
                            ?: ""

                    val deviceModel =
                        document.getString("deviceModel")
                            ?: ""

                    val description =
                        document.getString("description")
                            ?: ""

                    val appointmentDate =
                        document.getString("appointmentDate")
                            ?: ""

                    val status =
                        document.getString("status")
                            ?: "PENDING"

                    val categoryName =
                        getCategoryName(categoryId)

                    // Get service name from Firebase
                    db.collection("services")
                        .document(serviceId)
                        .get()
                        .addOnSuccessListener { serviceDocument ->

                            val serviceName =
                                serviceDocument.getString("name")
                                    ?: serviceId

                            addBookingCard(
                                categoryName = categoryName,
                                serviceName = serviceName,
                                price = price,
                                deviceBrand = deviceBrand,
                                deviceModel = deviceModel,
                                description = description,
                                appointmentDate = appointmentDate,
                                status = status
                            )
                        }
                        .addOnFailureListener {

                            addBookingCard(
                                categoryName = categoryName,
                                serviceName = serviceId,
                                price = price,
                                deviceBrand = deviceBrand,
                                deviceModel = deviceModel,
                                description = description,
                                appointmentDate = appointmentDate,
                                status = status
                            )
                        }
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

    private fun getCategoryName(categoryId: String): String {

        return when (categoryId) {

            "mobile" -> "Mobile"

            "laptop" -> "Laptop"

            "desktop" -> "Desktop"

            "gaming_console" -> "Gaming Console"

            else -> categoryId
        }
    }

    private fun addBookingCard(
        categoryName: String,
        serviceName: String,
        price: Int,
        deviceBrand: String,
        deviceModel: String,
        description: String,
        appointmentDate: String,
        status: String
    ) {

        // =========================================
        // BOOKING CARD
        // =========================================

        val bookingLayout = LinearLayout(this)

        bookingLayout.orientation =
            LinearLayout.VERTICAL

        bookingLayout.setPadding(
            40,
            32,
            40,
            32
        )

        bookingLayout.setBackgroundResource(
            R.drawable.bg_booking_card
        )

        // Same gray color as Device
        val detailColor =
            Color.parseColor("#94A3B8")

        // =========================================
        // CATEGORY
        // =========================================

        val categoryText = TextView(this)

        categoryText.text =
            "Category: $categoryName"

        categoryText.textSize = 18f

        categoryText.setTextColor(
            detailColor
        )

        // =========================================
        // SERVICE
        // =========================================

        val serviceText = TextView(this)

        serviceText.text =
            "Service: $serviceName"

        serviceText.textSize = 17f

        serviceText.setTextColor(
            detailColor
        )

        // =========================================
        // ESTIMATED PRICE
        // =========================================

        val priceText = TextView(this)

        priceText.text =
            "Estimated Price: Rs. $price"

        priceText.textSize = 17f

        priceText.setTextColor(
            detailColor
        )

        // =========================================
        // DEVICE
        // =========================================

        val deviceText = TextView(this)

        deviceText.text =
            "Device: $deviceBrand $deviceModel"

        deviceText.textSize = 16f

        deviceText.setTextColor(
            detailColor
        )

        // =========================================
        // PROBLEM
        // =========================================

        val descriptionText = TextView(this)

        descriptionText.text =
            "Problem: $description"

        descriptionText.textSize = 16f

        descriptionText.setTextColor(
            detailColor
        )

        // =========================================
        // APPOINTMENT DATE
        // =========================================

        val dateText = TextView(this)

        dateText.text =
            "Appointment Date: $appointmentDate"

        dateText.textSize = 16f

        dateText.setTextColor(
            detailColor
        )

        // =========================================
        // STATUS
        // =========================================

        val statusText = TextView(this)

        statusText.text =
            "Status: $status"

        statusText.textSize = 17f

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )

        // Keep status colors
        when (status.uppercase()) {

            "PENDING" -> {
                statusText.setTextColor(
                    Color.parseColor("#F59E0B")
                )
            }

            "CONFIRMED" -> {
                statusText.setTextColor(
                    Color.parseColor("#3B82F6")
                )
            }

            "DEVICE_RECEIVED",
            "DIAGNOSING",
            "REPAIRING",
            "QUALITY_CHECK",
            "READY_FOR_COLLECTION" -> {
                statusText.setTextColor(
                    Color.parseColor("#60A5FA")
                )
            }

            "COMPLETED" -> {
                statusText.setTextColor(
                    Color.parseColor("#22C55E")
                )
            }

            else -> {
                statusText.setTextColor(
                    Color.WHITE
                )
            }
        }

        // =========================================
        // ADD DETAILS TO CARD
        // =========================================

        bookingLayout.addView(categoryText)
        bookingLayout.addView(serviceText)
        bookingLayout.addView(priceText)
        bookingLayout.addView(deviceText)
        bookingLayout.addView(descriptionText)
        bookingLayout.addView(dateText)
        bookingLayout.addView(statusText)

        // =========================================
        // CARD MARGIN
        // =========================================

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            0,
            0,
            24
        )

        bookingsContainer.addView(
            bookingLayout,
            params
        )
    }
}
