package com.techfix.app.technician

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.techfix.app.R
import com.techfix.app.model.RepairStatus
import com.techfix.app.repository.RepairRequestRepository

class TechnicianDashboardActivity : AppCompatActivity() {

    private lateinit var txtTechnicianWelcome: TextView
    private lateinit var txtAssignedCount: TextView
    private lateinit var txtCompletedCount: TextView

    // Whole cards are clickable
    private lateinit var btnAssignedRepairs: MaterialCardView
    private lateinit var btnSpareParts: MaterialCardView
    private lateinit var btnRepairHistory: MaterialCardView

    private val repairRepository = RepairRequestRepository()

    private var technicianId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_technician_dashboard)

        initializeViews()

        // Get technician ID from login
        technicianId =
            intent.getStringExtra("technicianId") ?: ""

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

        // =========================================
        // ASSIGNED REPAIRS CARD
        // =========================================

        btnAssignedRepairs.setOnClickListener {

            val assignedIntent = Intent(
                this,
                AssignedRepairsActivity::class.java
            )

            assignedIntent.putExtra(
                "technicianId",
                technicianId
            )

            startActivity(assignedIntent)
        }

        // =========================================
        // SPARE PARTS CARD
        // =========================================

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

            val sparePartsIntent = Intent(
                this,
                SparePartsActivity::class.java
            )

            sparePartsIntent.putExtra(
                "branchId",
                branchId
            )

            sparePartsIntent.putExtra(
                "technicianId",
                technicianId
            )

            startActivity(sparePartsIntent)
        }

        // =========================================
        // REPAIR HISTORY CARD
        // =========================================

        btnRepairHistory.setOnClickListener {
            val historyIntent = Intent(
                this,
                TechnicianRepairHistoryActivity::class.java
            )

            historyIntent.putExtra(
                "technicianId",
                technicianId
            )

            startActivity(historyIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh counts when returning to dashboard
        if (technicianId.isNotEmpty()) {
            loadDashboardCounts()
        }
    }

    // =========================================
    // INITIALIZE VIEWS
    // =========================================

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

        btnRepairHistory =
            findViewById(R.id.btnRepairHistory)
    }

    // =========================================
    // LOAD DASHBOARD COUNTS
    // =========================================

    private fun loadDashboardCounts() {

        repairRepository.getRepairsByTechnician(

            technicianId = technicianId,

            onSuccess = { repairs ->

                val completedCount =
                    repairs.count { repair ->

                        val normalizedStatus =
                            repair.status
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
