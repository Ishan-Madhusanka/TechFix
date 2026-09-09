package com.techfix.app.technician

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.RepairStatus
import com.techfix.app.repository.RepairRequestRepository

class TechnicianRepairDetailsActivity : AppCompatActivity() {

    private lateinit var txtTechRepairId: TextView
    private lateinit var txtTechDevice: TextView
    private lateinit var txtTechDescription: TextView
    private lateinit var txtTechAppointment: TextView
    private lateinit var txtTechCurrentStatus: TextView

    private lateinit var spinnerRepairStatus: Spinner
    private lateinit var editRepairNotes: EditText

    private lateinit var btnUpdateStatus: MaterialButton
    private lateinit var btnSaveRepairNotes: MaterialButton

    private val repairRepository = RepairRequestRepository()

    private var repairId: String = ""

    private val statusList = listOf(
        RepairStatus.PENDING,
        RepairStatus.CONFIRMED,
        RepairStatus.DEVICE_RECEIVED,
        RepairStatus.DIAGNOSING,
        RepairStatus.REPAIRING,
        RepairStatus.QUALITY_CHECK,
        RepairStatus.READY_FOR_COLLECTION,
        RepairStatus.COMPLETED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technician_repair_details)

        initializeViews()
        setupStatusSpinner()

        repairId = intent.getStringExtra("repairId") ?: ""

        if (repairId.isEmpty()) {
            Toast.makeText(
                this,
                "Repair ID not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadRepairDetails()

        btnUpdateStatus.setOnClickListener {
            updateRepairStatus()
        }

        btnSaveRepairNotes.setOnClickListener {
            saveRepairNotes()
        }
    }

    private fun initializeViews() {
        txtTechRepairId = findViewById(R.id.txtTechRepairId)
        txtTechDevice = findViewById(R.id.txtTechDevice)
        txtTechDescription = findViewById(R.id.txtTechDescription)
        txtTechAppointment = findViewById(R.id.txtTechAppointment)
        txtTechCurrentStatus = findViewById(R.id.txtTechCurrentStatus)

        spinnerRepairStatus = findViewById(R.id.spinnerRepairStatus)
        editRepairNotes = findViewById(R.id.editRepairNotes)

        btnUpdateStatus = findViewById(R.id.btnUpdateStatus)
        btnSaveRepairNotes = findViewById(R.id.btnSaveRepairNotes)
    }

    private fun setupStatusSpinner() {

        val statusNames = statusList.map {
            it.name.replace("_", " ")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statusNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerRepairStatus.adapter = adapter
    }

    private fun loadRepairDetails() {

        repairRepository.getRepairById(
            repairId = repairId,

            onSuccess = { repair ->

                if (repair == null) {
                    Toast.makeText(
                        this,
                        "Repair request not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@getRepairById
                }

                txtTechRepairId.text = "Repair #${repair.id}"

                txtTechDevice.text =
                    "${repair.deviceBrand} ${repair.deviceModel}"

                txtTechDescription.text =
                    repair.description.ifEmpty {
                        "No problem description available."
                    }

                txtTechAppointment.text =
                    "Appointment: ${
                        repair.appointmentDate.ifEmpty {
                            "Not specified"
                        }
                    }"

                txtTechCurrentStatus.text =
                    repair.status.replace("_", " ")

                editRepairNotes.setText(repair.repairNotes)

                selectCurrentStatus(repair.status)
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to load repair: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun selectCurrentStatus(currentStatus: String) {

        // Supports both old "Pending" data
        // and new "PENDING" enum format.
        val normalizedStatus = currentStatus
            .trim()
            .uppercase()
            .replace(" ", "_")

        val position = statusList.indexOfFirst {
            it.name == normalizedStatus
        }

        if (position >= 0) {
            spinnerRepairStatus.setSelection(position)
        }
    }

    private fun updateRepairStatus() {

        val selectedStatus =
            statusList[spinnerRepairStatus.selectedItemPosition]

        btnUpdateStatus.isEnabled = false

        repairRepository.updateRepairStatus(
            repairId = repairId,
            status = selectedStatus,

            onSuccess = {

                btnUpdateStatus.isEnabled = true

                txtTechCurrentStatus.text =
                    selectedStatus.name.replace("_", " ")

                Toast.makeText(
                    this,
                    "Repair status updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onFailure = { exception ->

                btnUpdateStatus.isEnabled = true

                Toast.makeText(
                    this,
                    "Failed to update status: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun saveRepairNotes() {

        val notes = editRepairNotes.text
            .toString()
            .trim()

        if (notes.isEmpty()) {

            editRepairNotes.error =
                "Please enter repair notes"

            return
        }

        btnSaveRepairNotes.isEnabled = false

        repairRepository.updateRepairNotes(
            repairId = repairId,
            repairNotes = notes,

            onSuccess = {

                btnSaveRepairNotes.isEnabled = true

                Toast.makeText(
                    this,
                    "Repair notes saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onFailure = { exception ->

                btnSaveRepairNotes.isEnabled = true

                Toast.makeText(
                    this,
                    "Failed to save notes: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}