package com.techfix.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class BookRepairActivity : AppCompatActivity() {

    private lateinit var tvSelectedService: TextView
    private lateinit var etDeviceBrand: EditText
    private lateinit var etDeviceModel: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAppointmentDate: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnSubmitBooking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_repair)

        tvSelectedService = findViewById(R.id.tvSelectedService)
        etDeviceBrand = findViewById(R.id.etDeviceBrand)
        etDeviceModel = findViewById(R.id.etDeviceModel)
        etDescription = findViewById(R.id.etDescription)
        etAppointmentDate = findViewById(R.id.etAppointmentDate)
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

        // Upload image - functionality will be added next
        btnUploadImage.setOnClickListener {

            Toast.makeText(
                this,
                "Image upload coming next!",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Submit booking
        btnSubmitBooking.setOnClickListener {

            val brand = etDeviceBrand.text.toString().trim()
            val model = etDeviceModel.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val appointmentDate = etAppointmentDate.text.toString().trim()

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

            Toast.makeText(
                this,
                "Booking details are valid!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}