package com.techfix.app.technician

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techfix.app.R
import com.techfix.app.adapter.RepairAdapter
import com.techfix.app.model.RepairStatus
import com.techfix.app.repository.RepairRequestRepository

class TechnicianRepairHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerRepairHistory: RecyclerView
    private lateinit var progressRepairHistory: ProgressBar
    private lateinit var layoutRepairHistoryEmpty: LinearLayout
    private lateinit var repairAdapter: RepairAdapter

    private val repairRepository = RepairRequestRepository()

    private var technicianId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_repair_history)

        initializeViews()
        setupRecyclerView()

        technicianId = intent.getStringExtra("technicianId") ?: ""

        if (technicianId.isEmpty()) {
            showEmptyState()

            Toast.makeText(
                this,
                "Technician ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        loadRepairHistory()
    }

    private fun initializeViews() {
        recyclerRepairHistory =
            findViewById(R.id.recyclerRepairHistory)

        progressRepairHistory =
            findViewById(R.id.progressRepairHistory)

        layoutRepairHistoryEmpty =
            findViewById(R.id.layoutRepairHistoryEmpty)
    }

    private fun setupRecyclerView() {

        repairAdapter = RepairAdapter(emptyList()) { repair ->

            val detailsIntent = Intent(
                this,
                TechnicianRepairDetailsActivity::class.java
            )

            detailsIntent.putExtra(
                "repairId",
                repair.id
            )

            detailsIntent.putExtra(
                "technicianId",
                technicianId
            )

            startActivity(detailsIntent)
        }

        recyclerRepairHistory.layoutManager =
            LinearLayoutManager(this)

        recyclerRepairHistory.adapter =
            repairAdapter
    }

    private fun loadRepairHistory() {

        showLoading()

        repairRepository.getRepairsByTechnician(
            technicianId = technicianId,

            onSuccess = { repairs ->

                progressRepairHistory.visibility = View.GONE

                val completedRepairs = repairs.filter { repair ->

                    val normalizedStatus =
                        repair.status
                            .trim()
                            .uppercase()
                            .replace(" ", "_")

                    normalizedStatus == RepairStatus.COMPLETED.name
                }

                if (completedRepairs.isEmpty()) {

                    showEmptyState()

                } else {

                    layoutRepairHistoryEmpty.visibility = View.GONE
                    recyclerRepairHistory.visibility = View.VISIBLE

                    repairAdapter.updateData(completedRepairs)
                }
            },

            onFailure = { exception ->

                progressRepairHistory.visibility = View.GONE
                recyclerRepairHistory.visibility = View.GONE
                layoutRepairHistoryEmpty.visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    "Failed to load repair history: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading() {
        progressRepairHistory.visibility = View.VISIBLE
        recyclerRepairHistory.visibility = View.GONE
        layoutRepairHistoryEmpty.visibility = View.GONE
    }

    private fun showEmptyState() {
        progressRepairHistory.visibility = View.GONE
        recyclerRepairHistory.visibility = View.GONE
        layoutRepairHistoryEmpty.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        if (technicianId.isNotEmpty()) {
            loadRepairHistory()
        }
    }
}
