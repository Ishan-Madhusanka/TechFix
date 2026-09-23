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

    // Desktop RAM
    private lateinit var tvRamLabel: TextView
    private lateinit var etRam: EditText

    // Laptop RAM
    private lateinit var tvLaptopRamLabel: TextView
    private lateinit var etLaptopRam: EditText

    // Laptop Storage
    private lateinit var tvLaptopStorageLabel: TextView
    private lateinit var etLaptopStorage: EditText

    // Laptop Warranty
    private lateinit var tvLaptopWarrantyLabel: TextView
    private lateinit var etLaptopWarranty: EditText

    // Desktop Processor
    private lateinit var tvProcessorLabel: TextView
    private lateinit var etProcessor: EditText

    // Desktop Storage
    private lateinit var tvDesktopStorageLabel: TextView
    private lateinit var etDesktopStorage: EditText

    // Desktop Warranty
    private lateinit var tvDesktopWarrantyLabel: TextView
    private lateinit var etDesktopWarranty: EditText

    // Gaming Console Storage
    private lateinit var tvConsoleStorageLabel: TextView
    private lateinit var etConsoleStorage: EditText

    // Gaming Console Controller Count
    private lateinit var tvControllerCountLabel: TextView
    private lateinit var etControllerCount: EditText

    // Gaming Console Warranty
    private lateinit var tvConsoleWarrantyLabel: TextView
    private lateinit var etConsoleWarranty: EditText

    // Gaming Console Controller Problem
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

        // ---------------------------------------------------------
        // FIND VIEWS
        // ---------------------------------------------------------

        tvSelectedService =
            findViewById(R.id.tvSelectedService)

        tvDeviceBrandLabel =
            findViewById(R.id.tvDeviceBrandLabel)

        etDeviceBrand =
            findViewById(R.id.etDeviceBrand)

        tvDeviceModelLabel =
            findViewById(R.id.tvDeviceModelLabel)

        etDeviceModel =
            findViewById(R.id.etDeviceModel)

        tvOperatingSystemLabel =
            findViewById(R.id.tvOperatingSystemLabel)

        etOperatingSystem =
            findViewById(R.id.etOperatingSystem)

        // Desktop RAM
        tvRamLabel =
            findViewById(R.id.tvRamLabel)

        etRam =
            findViewById(R.id.etRam)

        // Laptop RAM
        tvLaptopRamLabel =
            findViewById(R.id.tvLaptopRamLabel)

        etLaptopRam =
            findViewById(R.id.etLaptopRam)

        // Laptop Storage
        tvLaptopStorageLabel =
            findViewById(R.id.tvLaptopStorageLabel)

        etLaptopStorage =
            findViewById(R.id.etLaptopStorage)

        // Laptop Warranty
        tvLaptopWarrantyLabel =
            findViewById(R.id.tvLaptopWarrantyLabel)

        etLaptopWarranty =
            findViewById(R.id.etLaptopWarranty)

        // Desktop Processor
        tvProcessorLabel =
            findViewById(R.id.tvProcessorLabel)

        etProcessor =
            findViewById(R.id.etProcessor)

        // Desktop Storage
        tvDesktopStorageLabel =
            findViewById(R.id.tvDesktopStorageLabel)

        etDesktopStorage =
            findViewById(R.id.etDesktopStorage)

        // Desktop Warranty
        tvDesktopWarrantyLabel =
            findViewById(R.id.tvDesktopWarrantyLabel)

        etDesktopWarranty =
            findViewById(R.id.etDesktopWarranty)

        // Console Storage
        tvConsoleStorageLabel =
            findViewById(R.id.tvConsoleStorageLabel)

        etConsoleStorage =
            findViewById(R.id.etConsoleStorage)

        // Controller Count
        tvControllerCountLabel =
            findViewById(R.id.tvControllerCountLabel)

        etControllerCount =
            findViewById(R.id.etControllerCount)

        // Console Warranty
        tvConsoleWarrantyLabel =
            findViewById(R.id.tvConsoleWarrantyLabel)

        etConsoleWarranty =
            findViewById(R.id.etConsoleWarranty)

        // Controller Problem
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

        // ---------------------------------------------------------
        // GET SELECTED SERVICE INFORMATION
        // ---------------------------------------------------------

        val serviceId =
            intent.getStringExtra("serviceId")

        val serviceName =
            intent.getStringExtra("serviceName")

        val categoryName =
            intent.getStringExtra("categoryName")

        val servicePrice =
            intent.getIntExtra("servicePrice", 0)

        // ---------------------------------------------------------
        // SETUP CATEGORY FIELDS
        // ---------------------------------------------------------

        setupCategoryFields(categoryName)

        // ---------------------------------------------------------
        // SHOW SELECTED SERVICE
        // ---------------------------------------------------------

        if (serviceName != null) {

            tvSelectedService.text =
                "Selected Service: $serviceName\nEstimated Price: Rs. $servicePrice"
        }

        // ---------------------------------------------------------
        // APPOINTMENT DATE
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

            datePickerDialog.show()
        }

        // ---------------------------------------------------------
        // UPLOAD IMAGE
        // ---------------------------------------------------------

        btnUploadImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        // ---------------------------------------------------------
        // TAKE PHOTO
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // SUBMIT BOOKING
        // ---------------------------------------------------------

        btnSubmitBooking.setOnClickListener {

            submitBooking(
                serviceId = serviceId,
                serviceName = serviceName,
                categoryName = categoryName,
                servicePrice = servicePrice
            )
        }
    }

    // =========================================================
    // CATEGORY-SPECIFIC FIELDS
    // =========================================================

    private fun setupCategoryFields(
        categoryName: String?
    ) {

        // ---------------------------------------------------------
        // HIDE ALL OPTIONAL FIELDS FIRST
        // ---------------------------------------------------------

        tvOperatingSystemLabel.visibility =
            View.GONE

        etOperatingSystem.visibility =
            View.GONE

        tvRamLabel.visibility =
            View.GONE

        etRam.visibility =
            View.GONE

        tvLaptopRamLabel.visibility =
            View.GONE

        etLaptopRam.visibility =
            View.GONE

        tvLaptopStorageLabel.visibility =
            View.GONE

        etLaptopStorage.visibility =
            View.GONE

        tvLaptopWarrantyLabel.visibility =
            View.GONE

        etLaptopWarranty.visibility =
            View.GONE

        tvProcessorLabel.visibility =
            View.GONE

        etProcessor.visibility =
            View.GONE

        tvDesktopStorageLabel.visibility =
            View.GONE

        etDesktopStorage.visibility =
            View.GONE

        tvDesktopWarrantyLabel.visibility =
            View.GONE

        etDesktopWarranty.visibility =
            View.GONE

        tvConsoleStorageLabel.visibility =
            View.GONE

        etConsoleStorage.visibility =
            View.GONE

        tvControllerCountLabel.visibility =
            View.GONE

        etControllerCount.visibility =
            View.GONE

        tvConsoleWarrantyLabel.visibility =
            View.GONE

        etConsoleWarranty.visibility =
            View.GONE

        tvControllerProblemLabel.visibility =
            View.GONE

        etControllerProblem.visibility =
            View.GONE

        // ---------------------------------------------------------
        // MOBILE
        // ---------------------------------------------------------

        when (categoryName) {

            "Mobile" -> {

                tvDeviceBrandLabel.text =
                    "Mobile Brand"

                etDeviceBrand.hint =
                    "Enter mobile brand"

                tvDeviceModelLabel.text =
                    "Mobile Model"

                etDeviceModel.hint =
                    "Enter mobile model"
            }

            // -----------------------------------------------------
            // LAPTOP
            // -----------------------------------------------------

            "Laptop" -> {

                tvDeviceBrandLabel.text =
                    "Laptop Brand"

                etDeviceBrand.hint =
                    "Enter laptop brand"

                tvDeviceModelLabel.text =
                    "Laptop Model"

                etDeviceModel.hint =
                    "Enter laptop model"

                // Operating System
                tvOperatingSystemLabel.visibility =
                    View.VISIBLE

                etOperatingSystem.visibility =
                    View.VISIBLE

                tvOperatingSystemLabel.text =
                    "Operating System"

                etOperatingSystem.hint =
                    "Example: Windows 11"

                // RAM
                tvLaptopRamLabel.visibility =
                    View.VISIBLE

                etLaptopRam.visibility =
                    View.VISIBLE

                tvLaptopRamLabel.text =
                    "RAM"

                etLaptopRam.hint =
                    "Example: 8GB"

                // Storage
                tvLaptopStorageLabel.visibility =
                    View.VISIBLE

                etLaptopStorage.visibility =
                    View.VISIBLE

                tvLaptopStorageLabel.text =
                    "Storage"

                etLaptopStorage.hint =
                    "Example: 512GB SSD"

                // Warranty - OPTIONAL
                tvLaptopWarrantyLabel.visibility =
                    View.VISIBLE

                etLaptopWarranty.visibility =
                    View.VISIBLE

                tvLaptopWarrantyLabel.text =
                    "Warranty Status (Optional)"

                etLaptopWarranty.hint =
                    "Example: Under Warranty / Expired"
            }

            // -----------------------------------------------------
            // DESKTOP
            // -----------------------------------------------------

            "Desktop" -> {

                tvDeviceBrandLabel.text =
                    "Desktop Brand"

                etDeviceBrand.hint =
                    "Enter desktop brand"

                tvDeviceModelLabel.text =
                    "Desktop Model"

                etDeviceModel.hint =
                    "Enter desktop model"

                // Operating System
                tvOperatingSystemLabel.visibility =
                    View.VISIBLE

                etOperatingSystem.visibility =
                    View.VISIBLE

                tvOperatingSystemLabel.text =
                    "Operating System"

                etOperatingSystem.hint =
                    "Example: Windows 11"

                // RAM - OPTIONAL
                tvRamLabel.visibility =
                    View.VISIBLE

                etRam.visibility =
                    View.VISIBLE

                tvRamLabel.text =
                    "RAM (Optional)"

                etRam.hint =
                    "Example: 8GB"

                // Processor - OPTIONAL
                tvProcessorLabel.visibility =
                    View.VISIBLE

                etProcessor.visibility =
                    View.VISIBLE

                tvProcessorLabel.text =
                    "Processor (Optional)"

                etProcessor.hint =
                    "Example: Intel Core i5"

                // Storage - OPTIONAL
                tvDesktopStorageLabel.visibility =
                    View.VISIBLE

                etDesktopStorage.visibility =
                    View.VISIBLE

                tvDesktopStorageLabel.text =
                    "Storage (Optional)"

                etDesktopStorage.hint =
                    "Example: 1TB HDD / 512GB SSD"

                // Warranty - OPTIONAL
                tvDesktopWarrantyLabel.visibility =
                    View.VISIBLE

                etDesktopWarranty.visibility =
                    View.VISIBLE

                tvDesktopWarrantyLabel.text =
                    "Warranty Status (Optional)"

                etDesktopWarranty.hint =
                    "Example: Under Warranty / Expired"
            }

            // -----------------------------------------------------
            // GAMING CONSOLE
            // -----------------------------------------------------

            "Gaming Console" -> {

                tvDeviceBrandLabel.text =
                    "Console Brand"

                etDeviceBrand.hint =
                    "Enter console brand"

                tvDeviceModelLabel.text =
                    "Console Model"

                etDeviceModel.hint =
                    "Enter console model"

                // Storage - OPTIONAL
                tvConsoleStorageLabel.visibility =
                    View.VISIBLE

                etConsoleStorage.visibility =
                    View.VISIBLE

                tvConsoleStorageLabel.text =
                    "Storage (Optional)"

                etConsoleStorage.hint =
                    "Example: 1TB"

                // Controller Count - OPTIONAL
                tvControllerCountLabel.visibility =
                    View.VISIBLE

                etControllerCount.visibility =
                    View.VISIBLE

                tvControllerCountLabel.text =
                    "Controller Count (Optional)"

                etControllerCount.hint =
                    "Example: 2"

                // Warranty - OPTIONAL
                tvConsoleWarrantyLabel.visibility =
                    View.VISIBLE

                etConsoleWarranty.visibility =
                    View.VISIBLE

                tvConsoleWarrantyLabel.text =
                    "Warranty Status (Optional)"

                etConsoleWarranty.hint =
                    "Example: Under Warranty / Expired"

                // Controller Problem - OPTIONAL
                tvControllerProblemLabel.visibility =
                    View.VISIBLE

                etControllerProblem.visibility =
                    View.VISIBLE

                tvControllerProblemLabel.text =
                    "Controller Problem (Optional)"

                etControllerProblem.hint =
                    "Describe controller problem if any"
            }
        }
    }

    // =========================================================
    // SUBMIT BOOKING
    // =========================================================

    private fun submitBooking(
        serviceId: String?,
        serviceName: String?,
        categoryName: String?,
        servicePrice: Int
    ) {

        // ---------------------------------------------------------
        // GET COMMON VALUES
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // GET LAPTOP VALUES
        // ---------------------------------------------------------

        val laptopRam =
            etLaptopRam.text.toString().trim()

        val laptopStorage =
            etLaptopStorage.text.toString().trim()

        val laptopWarranty =
            etLaptopWarranty.text.toString().trim()

        // ---------------------------------------------------------
        // GET DESKTOP VALUES
        // ---------------------------------------------------------

        val processor =
            etProcessor.text.toString().trim()

        val desktopStorage =
            etDesktopStorage.text.toString().trim()

        val desktopWarranty =
            etDesktopWarranty.text.toString().trim()

        // ---------------------------------------------------------
        // GET GAMING CONSOLE VALUES
        // ---------------------------------------------------------

        val consoleStorage =
            etConsoleStorage.text.toString().trim()

        val controllerCount =
            etControllerCount.text.toString().trim()

        val consoleWarranty =
            etConsoleWarranty.text.toString().trim()

        val currentUser =
            auth.currentUser

        // ---------------------------------------------------------
        // CHECK LOGIN
        // ---------------------------------------------------------

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please login before booking.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // ---------------------------------------------------------
        // VALIDATE BRAND
        // ---------------------------------------------------------

        if (brand.isEmpty()) {

            etDeviceBrand.error =
                "Enter device brand"

            return
        }

        // ---------------------------------------------------------
        // VALIDATE MODEL
        // ---------------------------------------------------------

        if (model.isEmpty()) {

            etDeviceModel.error =
                "Enter device model"

            return
        }

        // ---------------------------------------------------------
        // LAPTOP / DESKTOP OS VALIDATION
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // NOTE:
        // New device-specific fields are OPTIONAL.
        // No validation is required for:
        //
        // Laptop RAM
        // Laptop Storage
        // Laptop Warranty
        // Desktop RAM
        // Desktop Processor
        // Desktop Storage
        // Desktop Warranty
        // Console Storage
        // Controller Count
        // Console Warranty
        // Controller Problem
        // ---------------------------------------------------------

        // ---------------------------------------------------------
        // DESCRIPTION VALIDATION
        // ---------------------------------------------------------

        if (description.isEmpty()) {

            etDescription.error =
                "Describe the problem"

            return
        }

        // ---------------------------------------------------------
        // APPOINTMENT DATE VALIDATION
        // ---------------------------------------------------------

        if (appointmentDate.isEmpty()) {

            etAppointmentDate.error =
                "Select appointment date"

            return
        }

        // ---------------------------------------------------------
        // IMAGE VALIDATION
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // CATEGORY ID
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // SERVICE ID
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // DISABLE SUBMIT BUTTON
        // ---------------------------------------------------------

        btnSubmitBooking.isEnabled =
            false

        btnSubmitBooking.text =
            "Saving..."

        val selectedService =
            serviceName ?: "Unknown Service"

        val selectedCategory =
            categoryName ?: "Unknown Category"

        // ---------------------------------------------------------
        // CREATE FIRESTORE DOCUMENT
        // ---------------------------------------------------------

        val bookingReference =
            db.collection("repairRequests").document()

        val booking =
            hashMapOf(

                // Basic information
                "id" to bookingReference.id,

                "customerId" to currentUser.uid,

                "categoryId" to selectedCategoryId,

                "deviceBrand" to brand,

                "deviceModel" to model,

                // Existing fields
                "operatingSystem" to operatingSystem,

                "ram" to ram,

                "controllerProblem" to controllerProblem,

                // Laptop fields
                "laptopRam" to laptopRam,

                "laptopStorage" to laptopStorage,

                "laptopWarranty" to laptopWarranty,

                // Desktop fields
                "processor" to processor,

                "desktopStorage" to desktopStorage,

                "desktopWarranty" to desktopWarranty,

                // Gaming Console fields
                "consoleStorage" to consoleStorage,

                "controllerCount" to controllerCount,

                "consoleWarranty" to consoleWarranty,

                // Service information
                "serviceId" to selectedServiceId,

                "description" to description,

                "imageUrl" to imageUri.toString(),

                "appointmentDate" to appointmentDate,

                "price" to servicePrice.toDouble(),

                "status" to "PENDING",

                "createdAt" to FieldValue.serverTimestamp()
            )

        // ---------------------------------------------------------
        // SAVE TO FIRESTORE
        // ---------------------------------------------------------

        bookingReference
            .set(booking)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Booking submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // -------------------------------------------------
                // OPEN BOOKING CONFIRMATION
                // -------------------------------------------------

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

                btnSubmitBooking.isEnabled =
                    true

                btnSubmitBooking.text =
                    "Submit Booking"
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Booking failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()

                btnSubmitBooking.isEnabled =
                    true

                btnSubmitBooking.text =
                    "Submit Booking"
            }
    }

    // =========================================================
    // CREATE CAMERA IMAGE URI
    // =========================================================

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

    // =========================================================
    // OPEN CAMERA
    // =========================================================

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