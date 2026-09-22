package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RepairServicesActivity : AppCompatActivity() {

    private lateinit var tvCategoryName: TextView
    private lateinit var servicesContainer: LinearLayout

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_repair_services)

        tvCategoryName = findViewById(R.id.tvCategoryName)
        servicesContainer = findViewById(R.id.servicesContainer)

        // Get selected device category
        val categoryName = intent.getStringExtra("categoryName")

        if (categoryName == null) {
            Toast.makeText(
                this,
                "Device category not selected",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        tvCategoryName.text = "Services for: $categoryName"

        loadServices(categoryName)
    }

    private fun loadServices(categoryName: String) {

        // Convert category name to Firebase categoryId
        val categoryId = when (categoryName) {

            "Mobile" -> "mobile"

            "Laptop" -> "laptop"

            "Desktop" -> "desktop"

            "Gaming Console" -> "gaming_console"

            else -> categoryName.lowercase().replace(" ", "_")
        }

        db.collection("services")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .addOnSuccessListener { documents ->

                servicesContainer.removeAllViews()

                if (documents.isEmpty) {

                    val noServicesText = TextView(this)

                    noServicesText.text =
                        "No repair services available for $categoryName"

                    noServicesText.textSize = 18f

                    servicesContainer.addView(noServicesText)

                    return@addOnSuccessListener
                }

                for (document in documents) {

                    val serviceId = document.id

                    val serviceName =
                        document.getString("name")
                            ?: "Unknown Service"

                    val price =
                        document.getLong("price")?.toInt() ?: 0

                    val duration =
                        document.getString("duration")
                            ?: "Not specified"

                    addServiceButton(
                        serviceId = serviceId,
                        serviceName = serviceName,
                        price = price,
                        duration = duration,
                        categoryName = categoryName
                    )
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load services: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun addServiceButton(
        serviceId: String,
        serviceName: String,
        price: Int,
        duration: String,
        categoryName: String
    ) {

        val button = Button(this)

        button.text =
            "$serviceName\nRs. $price\nDuration: $duration"

        button.textSize = 16f

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            0,
            0,
            16
        )

        servicesContainer.addView(
            button,
            params
        )

        button.setOnClickListener {

            val intent = Intent(
                this,
                ServiceDetailsActivity::class.java
            )

            intent.putExtra(
                "serviceId",
                serviceId
            )

            intent.putExtra(
                "serviceName",
                serviceName
            )

            intent.putExtra(
                "categoryName",
                categoryName
            )

            intent.putExtra(
                "servicePrice",
                price
            )

            intent.putExtra(
                "duration",
                duration
            )

            startActivity(intent)
        }
    }
}