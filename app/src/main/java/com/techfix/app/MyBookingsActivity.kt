package com.techfix.app

import android.content.Intent
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
                val bookings = documents.documents.sortedByDescending {
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

        val bookingLayout = LinearLayout(this)

        bookingLayout.orientation =
            LinearLayout.VERTICAL

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
            "Estimated Price: Rs. $price"

        priceText.textSize = 17f

        priceText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val deviceText = TextView(this)

        deviceText.text =
            "Device: $deviceBrand $deviceModel"

        deviceText.textSize = 16f

        val descriptionText = TextView(this)

        descriptionText.text =
            "Problem: $description"

        descriptionText.textSize = 16f

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
        bookingLayout.addView(descriptionText)
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

        // Open booking details when the booking card is clicked
        bookingLayout.setOnClickListener {

            val intent = Intent(
                this,
                BookingDetailsActivity::class.java
            )

            intent.putExtra(
                "categoryName",
                categoryName
            )

            intent.putExtra(
                "serviceName",
                serviceName
            )

            intent.putExtra(
                "price",
                price
            )

            intent.putExtra(
                "deviceBrand",
                deviceBrand
            )

            intent.putExtra(
                "deviceModel",
                deviceModel
            )

            intent.putExtra(
                "description",
                description
            )

            intent.putExtra(
                "appointmentDate",
                appointmentDate
            )

            intent.putExtra(
                "status",
                status
            )

            startActivity(intent)
        }
    }
}