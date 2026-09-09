package com.techfix.app.repair

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.repository.RepairRequestRepository
import java.text.NumberFormat
import java.util.Locale

class RepairDetailsActivity : AppCompatActivity() {

    private lateinit var txtRepairId: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDevice: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtAppointmentDate: TextView
    private lateinit var txtBranch: TextView
    private lateinit var txtTechnician: TextView
    private lateinit var txtRepairNotes: TextView
    private lateinit var txtPrice: TextView
    private lateinit var btnTrackRepair: MaterialButton

    private val repairRepository = RepairRequestRepository()

    private var repairId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair_details)

        initializeViews()

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

        btnTrackRepair.setOnClickListener {

            val intent = android.content.Intent(
                this,
                RepairTrackingActivity::class.java
            )

            intent.putExtra("repairId", repairId)

            startActivity(intent)
        }
    }

    private fun initializeViews() {
        txtRepairId = findViewById(R.id.txtRepairId)
        txtStatus = findViewById(R.id.txtStatus)
        txtDevice = findViewById(R.id.txtDevice)
        txtDescription = findViewById(R.id.txtDescription)
        txtAppointmentDate = findViewById(R.id.txtAppointmentDate)
        txtBranch = findViewById(R.id.txtBranch)
        txtTechnician = findViewById(R.id.txtTechnician)
        txtRepairNotes = findViewById(R.id.txtRepairNotes)
        txtPrice = findViewById(R.id.txtPrice)
        btnTrackRepair = findViewById(R.id.btnTrackRepair)
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

                txtRepairId.text = "Repair #${repair.id}"

                txtStatus.text =
                    repair.status.replace("_", " ")

                txtDevice.text =
                    "${repair.deviceBrand} ${repair.deviceModel}"

                txtDescription.text =
                    repair.description.ifEmpty {
                        "No problem description available."
                    }

                txtAppointmentDate.text =
                    repair.appointmentDate.ifEmpty {
                        "Not specified"
                    }

                // Currently IDs are shown.
                // Later integration can resolve these IDs to names.
                txtBranch.text =
                    repair.branchId.ifEmpty {
                        "Not Assigned"
                    }

                txtTechnician.text =
                    repair.technicianId.ifEmpty {
                        "Not Assigned"
                    }

                txtRepairNotes.text =
                    repair.repairNotes.ifEmpty {
                        "No repair notes available."
                    }

                val formatter =
                    NumberFormat.getNumberInstance(Locale.US)

                txtPrice.text =
                    "LKR ${formatter.format(repair.price)}"
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
}