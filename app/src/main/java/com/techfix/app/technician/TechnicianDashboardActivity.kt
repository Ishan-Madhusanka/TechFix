package com.techfix.app.technician

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.RepairStatus
import com.techfix.app.repository.RepairRequestRepository

class TechnicianDashboardActivity : AppCompatActivity() {

    private lateinit var txtTechnicianWelcome: TextView
    private lateinit var txtAssignedCount: TextView
    private lateinit var txtCompletedCount: TextView

    private lateinit var btnAssignedRepairs: MaterialButton
    private lateinit var btnSpareParts: MaterialButton

    private val repairRepository = RepairRequestRepository()

    private var technicianId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technician_dashboard)

        initializeViews()

        technicianId = intent.getStringExtra("technicianId") ?: ""

        if (technicianId.isEmpty()) {
            Toast.makeText(
                this,
                "Technician ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        txtTechnicianWelcome.text =
            "Manage your assigned repairs"

        loadDashboardCounts()

        btnAssignedRepairs.setOnClickListener {

            val intent = Intent(
                this,
                AssignedRepairsActivity::class.java
            )

            intent.putExtra("technicianId", technicianId)

            startActivity(intent)
        }

        btnSpareParts.setOnClickListener {

            val branchId =
                intent.getStringExtra("branchId") ?: ""

            if (branchId.isEmpty()) {

                Toast.makeText(
                    this,
                    "Branch ID not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(
                this,
                SparePartsActivity::class.java
            )

            intent.putExtra("branchId", branchId)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (technicianId.isNotEmpty()) {
            loadDashboardCounts()
        }
    }

    private fun initializeViews() {

        txtTechnicianWelcome =
            findViewById(R.id.txtTechnicianWelcome)

        txtAssignedCount =
            findViewById(R.id.txtAssignedCount)

        txtCompletedCount =
            findViewById(R.id.txtCompletedCount)

        btnAssignedRepairs =
            findViewById(R.id.btnAssignedRepairs)

        btnSpareParts =
            findViewById(R.id.btnSpareParts)
    }

    private fun loadDashboardCounts() {

        repairRepository.getRepairsByTechnician(

            technicianId = technicianId,

            onSuccess = { repairs ->

                val completedCount = repairs.count { repair ->

                    val normalizedStatus = repair.status
                        .trim()
                        .uppercase()
                        .replace(" ", "_")

                    normalizedStatus ==
                            RepairStatus.COMPLETED.name
                }

                txtAssignedCount.text =
                    repairs.size.toString()

                txtCompletedCount.text =
                    completedCount.toString()
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to load dashboard: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}