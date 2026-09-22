package com.techfix.app

import android.app.DatePickerDialog
import android.content.Intent
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

    // Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    // ---------------------------------------------------------
    // IMAGE PICKER
    // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // CONNECT XML VIEWS
        // ---------------------------------------------------------

        tvSelectedService =
            findViewById(R.id.tvSelectedService)

        etDeviceBrand =
            findViewById(R.id.etDeviceBrand)

        etDeviceModel =
            findViewById(R.id.etDeviceModel)

        etDescription =
            findViewById(R.id.etDescription)

        etAppointmentDate =
            findViewById(R.id.etAppointmentDate)

        ivDamageImage =
            findViewById(R.id.ivDamageImage)

        btnUploadImage =
            findViewById(R.id.btnUploadImage)

        btnSubmitBooking =
            findViewById(R.id.btnSubmitBooking)

        // ---------------------------------------------------------
        // RECEIVE MEMBER 2 SERVICE DATA
        // ---------------------------------------------------------

        val serviceId =
            intent.getStringExtra("SERVICE_ID")

        val serviceName =
            intent.getStringExtra("serviceName")

        val categoryName =
            intent.getStringExtra("categoryName")

        val servicePrice =
            intent.getIntExtra(
                "servicePrice",
                0
            )

        // ---------------------------------------------------------
        // DISPLAY SELECTED SERVICE
        // ---------------------------------------------------------

        if (serviceName != null) {

            tvSelectedService.text =
                "Selected Service: $serviceName\n" +
                        "Estimated Price: Rs. $servicePrice"
        }

        // ---------------------------------------------------------
        // APPOINTMENT DATE PICKER
        // ---------------------------------------------------------

        etAppointmentDate.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            val year =
                calendar.get(Calendar.YEAR)

            val month =
                calendar.get(Calendar.MONTH)

            val day =
                calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->

                        val formattedDate =
                            String.format(
                                "%04d-%02d-%02d",
                                selectedYear,
                                selectedMonth + 1,
                                selectedDay
                            )

                        etAppointmentDate.setText(
                            formattedDate
                        )
                    },
                    year,
                    month,
                    day
                )

            // Prevent selecting past dates
            datePickerDialog.datePicker.minDate =
                System.currentTimeMillis() - 1000

            datePickerDialog.show()
        }

        // ---------------------------------------------------------
        // SELECT DAMAGE IMAGE - OPTIONAL
        // ---------------------------------------------------------

        btnUploadImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        // ---------------------------------------------------------
        // SUBMIT BOOKING
        // ---------------------------------------------------------

        btnSubmitBooking.setOnClickListener {

            val brand =
                etDeviceBrand.text.toString().trim()

            val model =
                etDeviceModel.text.toString().trim()

            val description =
                etDescription.text.toString().trim()

            val appointmentDate =
                etAppointmentDate.text.toString().trim()

            val currentUser =
                auth.currentUser

            // -----------------------------------------------------
            // VALIDATION
            // -----------------------------------------------------

            if (currentUser == null) {

                Toast.makeText(
                    this,
                    "Please login before booking.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (brand.isEmpty()) {

                etDeviceBrand.error =
                    "Enter device brand"

                return@setOnClickListener
            }

            if (model.isEmpty()) {

                etDeviceModel.error =
                    "Enter device model"

                return@setOnClickListener
            }

            if (description.isEmpty()) {

                etDescription.error =
                    "Describe the problem"

                return@setOnClickListener
            }

            if (appointmentDate.isEmpty()) {

                etAppointmentDate.error =
                    "Select appointment date"

                return@setOnClickListener
            }

            // Damage image is OPTIONAL
            // No validation required for selectedImageUri

            // Member 1 MainActivity requires SERVICE_ID
            if (serviceId.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    "This service is not available for branch selection",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // -----------------------------------------------------
            // SAVE MEMBER 2 BOOKING DETAILS TEMPORARILY
            // -----------------------------------------------------

            val bookingPreferences =
                getSharedPreferences(
                    "TECHFIX_BOOKING_DATA",
                    MODE_PRIVATE
                )

            bookingPreferences
                .edit()
                .putString(
                    "customerId",
                    currentUser.uid
                )
                .putString(
                    "customerEmail",
                    currentUser.email ?: ""
                )
                .putString(
                    "categoryName",
                    categoryName ?: ""
                )
                .putString(
                    "serviceName",
                    serviceName ?: ""
                )
                .putString(
                    "deviceBrand",
                    brand
                )
                .putString(
                    "deviceModel",
                    model
                )
                .putString(
                    "description",
                    description
                )
                .putString(
                    "appointmentDate",
                    appointmentDate
                )
                .putString(
                    "imageUri",
                    selectedImageUri?.toString() ?: ""
                )
                .putInt(
                    "servicePrice",
                    servicePrice
                )
                .apply()

            // -----------------------------------------------------
            // OPEN MEMBER 1 SUITABLE BRANCH LOGIC
            // -----------------------------------------------------

            val suitableBranchIntent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            // Member 1 expects this exact key
            suitableBranchIntent.putExtra(
                "SERVICE_ID",
                serviceId
            )

            startActivity(
                suitableBranchIntent
            )
        }
    }
}