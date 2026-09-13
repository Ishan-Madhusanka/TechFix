package com.techfix.app

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BookRepairActivity : AppCompatActivity() {

    private lateinit var tvSelectedService: TextView
    private lateinit var etDeviceBrand: EditText
    private lateinit var etDeviceModel: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAppointmentDate: EditText
    private lateinit var ivDamageImage: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var btnSubmitBooking: Button

    private var selectedImageUri: Uri? = null

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Gallery image picker
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                selectedImageUri = uri

                ivDamageImage.setImageURI(uri)
                ivDamageImage.visibility = ImageView.VISIBLE

                Toast.makeText(
                    this,
                    "Image selected successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_repair)

        tvSelectedService = findViewById(R.id.tvSelectedService)
        etDeviceBrand = findViewById(R.id.etDeviceBrand)
        etDeviceModel = findViewById(R.id.etDeviceModel)
        etDescription = findViewById(R.id.etDescription)
        etAppointmentDate = findViewById(R.id.etAppointmentDate)
        ivDamageImage = findViewById(R.id.ivDamageImage)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnSubmitBooking = findViewById(R.id.btnSubmitBooking)

        // Get selected service
        val serviceName = intent.getStringExtra("serviceName")

        if (serviceName != null) {
            tvSelectedService.text = "Selected Service: $serviceName"
        }

        // Open calendar when date field is clicked
        etAppointmentDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val formattedDate = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )

                    etAppointmentDate.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        // Open gallery
        btnUploadImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        // Submit booking
        btnSubmitBooking.setOnClickListener {

            val brand = etDeviceBrand.text.toString().trim()
            val model = etDeviceModel.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val appointmentDate = etAppointmentDate.text.toString().trim()

            val currentUser = auth.currentUser

            // Check logged-in user
            if (currentUser == null) {
                Toast.makeText(
                    this,
                    "Please login before booking.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validation
            if (brand.isEmpty()) {
                etDeviceBrand.error = "Enter device brand"
                return@setOnClickListener
            }

            if (model.isEmpty()) {
                etDeviceModel.error = "Enter device model"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                etDescription.error = "Describe the problem"
                return@setOnClickListener
            }

            if (appointmentDate.isEmpty()) {
                etAppointmentDate.error = "Select appointment date"
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(
                    this,
                    "Please upload a damage image",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Disable button while saving
            btnSubmitBooking.isEnabled = false
            btnSubmitBooking.text = "Saving..."

            // Selected service
            val selectedService = serviceName ?: "Unknown Service"

            // Booking data
            val booking = hashMapOf(
                "customerId" to currentUser.uid,
                "customerEmail" to (currentUser.email ?: ""),
                "serviceName" to selectedService,
                "deviceBrand" to brand,
                "deviceModel" to model,
                "description" to description,
                "appointmentDate" to appointmentDate,

                // Option 2:
                // Image is NOT uploaded to Firebase Storage.
                "imageSelected" to true,
                "imageUri" to selectedImageUri.toString(),

                // Initial repair status
                "status" to "PENDING",

                // Created time
                "createdAt" to FieldValue.serverTimestamp()
            )

            // Save booking to Firestore
            db.collection("repairRequests")
                .add(booking)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Booking submitted successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    btnSubmitBooking.isEnabled = true
                    btnSubmitBooking.text = "Submit Booking"

                    // Clear form
                    etDeviceBrand.text.clear()
                    etDeviceModel.text.clear()
                    etDescription.text.clear()
                    etAppointmentDate.text.clear()

                    ivDamageImage.setImageDrawable(null)
                    ivDamageImage.visibility = ImageView.GONE

                    selectedImageUri = null
                }
                .addOnFailureListener { error ->

                    Toast.makeText(
                        this,
                        "Booking failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    btnSubmitBooking.isEnabled = true
                    btnSubmitBooking.text = "Submit Booking"
                }
        }
    }
}