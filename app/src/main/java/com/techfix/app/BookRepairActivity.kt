package com.techfix.app

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BookRepairActivity : AppCompatActivity() {

    private lateinit var tvSelectedService: TextView

    private lateinit var tvDeviceBrandLabel: TextView
    private lateinit var etDeviceBrand: EditText

    private lateinit var tvDeviceModelLabel: TextView
    private lateinit var etDeviceModel: EditText

    private lateinit var tvOperatingSystemLabel: TextView
    private lateinit var etOperatingSystem: EditText

    private lateinit var tvRamLabel: TextView
    private lateinit var etRam: EditText

    private lateinit var tvControllerProblemLabel: TextView
    private lateinit var etControllerProblem: EditText

    private lateinit var etDescription: EditText
    private lateinit var etAppointmentDate: EditText

    private lateinit var ivDamageImage: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSubmitBooking: Button

    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null

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
                ivDamageImage.visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    "Image selected successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Camera
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            val uri = cameraImageUri

            if (success && uri != null) {

                selectedImageUri = uri

                ivDamageImage.setImageURI(uri)
                ivDamageImage.visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    "Photo captured successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Camera permission
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {

                openCamera()

            } else {

                Toast.makeText(
                    this,
                    "Camera permission is required.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_repair)

        // Find views
        tvSelectedService = findViewById(R.id.tvSelectedService)

        tvDeviceBrandLabel = findViewById(R.id.tvDeviceBrandLabel)
        etDeviceBrand = findViewById(R.id.etDeviceBrand)

        tvDeviceModelLabel = findViewById(R.id.tvDeviceModelLabel)
        etDeviceModel = findViewById(R.id.etDeviceModel)

        tvOperatingSystemLabel =
            findViewById(R.id.tvOperatingSystemLabel)

        etOperatingSystem =
            findViewById(R.id.etOperatingSystem)

        tvRamLabel =
            findViewById(R.id.tvRamLabel)

        etRam =
            findViewById(R.id.etRam)

        tvControllerProblemLabel =
            findViewById(R.id.tvControllerProblemLabel)

        etControllerProblem =
            findViewById(R.id.etControllerProblem)

        etDescription =
            findViewById(R.id.etDescription)

        etAppointmentDate =
            findViewById(R.id.etAppointmentDate)

        ivDamageImage =
            findViewById(R.id.ivDamageImage)

        btnUploadImage =
            findViewById(R.id.btnUploadImage)

        btnTakePhoto =
            findViewById(R.id.btnTakePhoto)

        btnSubmitBooking =
            findViewById(R.id.btnSubmitBooking)

        // Get selected service information
        val serviceId =
            intent.getStringExtra("serviceId")

        val serviceName =
            intent.getStringExtra("serviceName")

        val categoryName =
            intent.getStringExtra("categoryName")

        val servicePrice =
            intent.getIntExtra("servicePrice", 0)

        // Change fields according to device category
        setupCategoryFields(categoryName)

        // Show selected service
        if (serviceName != null) {

            tvSelectedService.text =
                "Selected Service: $serviceName\nEstimated Price: Rs. $servicePrice"
        }

        // Appointment Date
        etAppointmentDate.setOnClickListener {

            val calendar = Calendar.getInstance()

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

            datePickerDialog.show()
        }

        // Upload Damage Image
        btnUploadImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        // Take Photo
        btnTakePhoto.setOnClickListener {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                openCamera()

            } else {

                cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }

        // Submit Booking
        btnSubmitBooking.setOnClickListener {

            submitBooking(
                serviceId = serviceId,
                serviceName = serviceName,
                categoryName = categoryName,
                servicePrice = servicePrice
            )
        }
    }

    // ---------------------------------------------------------
    // CATEGORY-SPECIFIC FIELDS
    // ---------------------------------------------------------

    private fun setupCategoryFields(categoryName: String?) {

        // Hide optional fields first
        tvOperatingSystemLabel.visibility = View.GONE
        etOperatingSystem.visibility = View.GONE

        tvRamLabel.visibility = View.GONE
        etRam.visibility = View.GONE

        tvControllerProblemLabel.visibility = View.GONE
        etControllerProblem.visibility = View.GONE

        when (categoryName) {

            "Mobile" -> {

                tvDeviceBrandLabel.text = "Mobile Brand"
                etDeviceBrand.hint = "Enter mobile brand"

                tvDeviceModelLabel.text = "Mobile Model"
                etDeviceModel.hint = "Enter mobile model"
            }

            "Laptop" -> {

                tvDeviceBrandLabel.text = "Laptop Brand"
                etDeviceBrand.hint = "Enter laptop brand"

                tvDeviceModelLabel.text = "Laptop Model"
                etDeviceModel.hint = "Enter laptop model"

                tvOperatingSystemLabel.visibility = View.VISIBLE
                etOperatingSystem.visibility = View.VISIBLE

                tvOperatingSystemLabel.text =
                    "Operating System"

                etOperatingSystem.hint =
                    "Example: Windows 11"
            }

            "Desktop" -> {

                tvDeviceBrandLabel.text = "Desktop Brand"
                etDeviceBrand.hint = "Enter desktop brand"

                tvDeviceModelLabel.text = "Desktop Model"
                etDeviceModel.hint = "Enter desktop model"

                tvOperatingSystemLabel.visibility = View.VISIBLE
                etOperatingSystem.visibility = View.VISIBLE

                tvOperatingSystemLabel.text =
                    "Operating System"

                etOperatingSystem.hint =
                    "Example: Windows 11"

                tvRamLabel.visibility = View.VISIBLE
                etRam.visibility = View.VISIBLE

                tvRamLabel.text = "RAM"

                etRam.hint =
                    "Example: 8GB"
            }

            "Gaming Console" -> {

                tvDeviceBrandLabel.text = "Console Brand"
                etDeviceBrand.hint = "Enter console brand"

                tvDeviceModelLabel.text = "Console Model"
                etDeviceModel.hint = "Enter console model"

                tvControllerProblemLabel.visibility =
                    View.VISIBLE

                etControllerProblem.visibility =
                    View.VISIBLE

                tvControllerProblemLabel.text =
                    "Controller Problem"

                etControllerProblem.hint =
                    "Describe controller problem if any"
            }
        }
    }

    // ---------------------------------------------------------
    // SUBMIT BOOKING
    // ---------------------------------------------------------

    private fun submitBooking(
        serviceId: String?,
        serviceName: String?,
        categoryName: String?,
        servicePrice: Int
    ) {

        val brand =
            etDeviceBrand.text.toString().trim()

        val model =
            etDeviceModel.text.toString().trim()

        val operatingSystem =
            etOperatingSystem.text.toString().trim()

        val ram =
            etRam.text.toString().trim()

        val controllerProblem =
            etControllerProblem.text.toString().trim()

        val description =
            etDescription.text.toString().trim()

        val appointmentDate =
            etAppointmentDate.text.toString().trim()

        val currentUser =
            auth.currentUser

        // Check login
        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please login before booking.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validate brand
        if (brand.isEmpty()) {

            etDeviceBrand.error =
                "Enter device brand"

            return
        }

        // Validate model
        if (model.isEmpty()) {

            etDeviceModel.error =
                "Enter device model"

            return
        }

        // Laptop / Desktop OS validation
        if (
            categoryName == "Laptop" ||
            categoryName == "Desktop"
        ) {

            if (operatingSystem.isEmpty()) {

                etOperatingSystem.error =
                    "Enter operating system"

                return
            }
        }

        // Desktop RAM validation
        if (categoryName == "Desktop") {

            if (ram.isEmpty()) {

                etRam.error =
                    "Enter RAM"

                return
            }
        }

        // Controller problem is optional
        // for Gaming Console

        // Validate description
        if (description.isEmpty()) {

            etDescription.error =
                "Describe the problem"

            return
        }

        // Validate appointment date
        if (appointmentDate.isEmpty()) {

            etAppointmentDate.error =
                "Select appointment date"

            return
        }

        // Validate image
        val imageUri =
            selectedImageUri

        if (imageUri == null) {

            Toast.makeText(
                this,
                "Please upload a damage image or take a photo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Category ID
        val selectedCategoryId =
            when (categoryName) {

                "Mobile" ->
                    "mobile"

                "Laptop" ->
                    "laptop"

                "Desktop" ->
                    "desktop"

                "Gaming Console" ->
                    "gaming_console"

                else ->
                    categoryName
                        ?.lowercase()
                        ?.replace(" ", "_")
                        ?: ""
            }

        // Service ID
        val selectedServiceId =
            serviceId ?: ""

        if (selectedServiceId.isEmpty()) {

            Toast.makeText(
                this,
                "Service information missing.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (selectedCategoryId.isEmpty()) {

            Toast.makeText(
                this,
                "Category information missing.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Disable submit button
        btnSubmitBooking.isEnabled = false
        btnSubmitBooking.text = "Saving..."

        val selectedService =
            serviceName ?: "Unknown Service"

        val selectedCategory =
            categoryName ?: "Unknown Category"

        // Create Firestore document
        val bookingReference =
            db.collection("repairRequests").document()

        val booking =
            hashMapOf(

                "id" to bookingReference.id,

                "customerId" to currentUser.uid,

                "categoryId" to selectedCategoryId,

                "deviceBrand" to brand,

                "deviceModel" to model,

                "operatingSystem" to operatingSystem,

                "ram" to ram,

                "controllerProblem" to controllerProblem,

                "serviceId" to selectedServiceId,

                "description" to description,

                "imageUrl" to imageUri.toString(),

                "appointmentDate" to appointmentDate,

                "price" to servicePrice.toDouble(),

                "status" to "PENDING",

                "createdAt" to FieldValue.serverTimestamp()
            )

        // Save to Firestore
        bookingReference
            .set(booking)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Booking submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Open confirmation
                val confirmationIntent =
                    Intent(
                        this,
                        BookingConfirmationActivity::class.java
                    )

                confirmationIntent.putExtra(
                    "serviceName",
                    selectedService
                )

                confirmationIntent.putExtra(
                    "categoryName",
                    selectedCategory
                )

                confirmationIntent.putExtra(
                    "deviceBrand",
                    brand
                )

                confirmationIntent.putExtra(
                    "deviceModel",
                    model
                )

                confirmationIntent.putExtra(
                    "appointmentDate",
                    appointmentDate
                )

                confirmationIntent.putExtra(
                    "servicePrice",
                    servicePrice
                )

                startActivity(
                    confirmationIntent
                )

                btnSubmitBooking.isEnabled = true
                btnSubmitBooking.text =
                    "Submit Booking"
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Booking failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()

                btnSubmitBooking.isEnabled = true
                btnSubmitBooking.text =
                    "Submit Booking"
            }
    }

    // ---------------------------------------------------------
    // CREATE CAMERA IMAGE URI
    // ---------------------------------------------------------

    private fun createImageUri(): Uri? {

        val contentValues =
            ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "techfix_${System.currentTimeMillis()}.jpg"
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/jpeg"
                )
            }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    // ---------------------------------------------------------
    // OPEN CAMERA
    // ---------------------------------------------------------

    private fun openCamera() {

        val uri =
            createImageUri()

        cameraImageUri =
            uri

        if (uri != null) {

            cameraLauncher.launch(uri)

        } else {

            Toast.makeText(
                this,
                "Unable to open camera.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}