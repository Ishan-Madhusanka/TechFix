package com.techfix.app.technician

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
import com.techfix.app.repository.RepairRequestRepository

class AssignedRepairsActivity : AppCompatActivity() {

    private lateinit var recyclerAssignedRepairs: RecyclerView
    private lateinit var progressAssignedRepairs: ProgressBar
    private lateinit var layoutAssignedEmpty: LinearLayout
    private lateinit var repairAdapter: RepairAdapter

    private val repairRepository = RepairRequestRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assigned_repairs)

        initializeViews()
        setupRecyclerView()
        loadAssignedRepairs()
    }

    private fun initializeViews() {
        recyclerAssignedRepairs =
            findViewById(R.id.recyclerAssignedRepairs)

        progressAssignedRepairs =
            findViewById(R.id.progressAssignedRepairs)

        layoutAssignedEmpty =
            findViewById(R.id.layoutAssignedEmpty)
    }

    private fun setupRecyclerView() {

        repairAdapter = RepairAdapter(emptyList()) { repair ->

            val intent = android.content.Intent(
                this,
                TechnicianRepairDetailsActivity::class.java
            )

            intent.putExtra("repairId", repair.id)

            startActivity(intent)
        }

        recyclerAssignedRepairs.layoutManager =
            LinearLayoutManager(this)

        recyclerAssignedRepairs.adapter =
            repairAdapter
    }

    private fun loadAssignedRepairs() {

        val technicianId =
            intent.getStringExtra("technicianId")

        if (technicianId.isNullOrEmpty()) {
            showEmptyState()

            Toast.makeText(
                this,
                "Technician ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        showLoading()

        repairRepository.getRepairsByTechnician(

            technicianId = technicianId,

            onSuccess = { repairs ->

                progressAssignedRepairs.visibility = View.GONE

                if (repairs.isEmpty()) {

                    showEmptyState()

                } else {

                    layoutAssignedEmpty.visibility = View.GONE
                    recyclerAssignedRepairs.visibility = View.VISIBLE

                    repairAdapter.updateData(repairs)
                }
            },

            onFailure = { exception ->

                progressAssignedRepairs.visibility = View.GONE
                recyclerAssignedRepairs.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Failed to load assigned repairs: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading() {

        progressAssignedRepairs.visibility = View.VISIBLE
        recyclerAssignedRepairs.visibility = View.GONE
        layoutAssignedEmpty.visibility = View.GONE
    }

    private fun showEmptyState() {

        progressAssignedRepairs.visibility = View.GONE
        recyclerAssignedRepairs.visibility = View.GONE
        layoutAssignedEmpty.visibility = View.VISIBLE
    }
}