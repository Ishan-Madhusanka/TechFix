package com.techfix.app.repair

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

class RepairHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerRepairHistory: RecyclerView
    private lateinit var progressRepairHistory: ProgressBar
    private lateinit var layoutRepairHistoryEmpty: LinearLayout
    private lateinit var repairAdapter: RepairAdapter

    private val repairRepository = RepairRequestRepository()

    private var customerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair_history)

        initializeViews()
        setupRecyclerView()

        customerId = intent.getStringExtra("customerId") ?: ""

        if (customerId.isEmpty()) {
            showEmptyState()

            Toast.makeText(
                this,
                "Customer ID not found",
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

            val intent = android.content.Intent(
                this,
                RepairDetailsActivity::class.java
            )

            intent.putExtra(
                "repairId",
                repair.id
            )

            startActivity(intent)
        }

        recyclerRepairHistory.layoutManager =
            LinearLayoutManager(this)

        recyclerRepairHistory.adapter =
            repairAdapter
    }

    private fun loadRepairHistory() {

        showLoading()

        repairRepository.getRepairHistory(
            customerId = customerId,

            onSuccess = { repairs ->

                progressRepairHistory.visibility = View.GONE

                if (repairs.isEmpty()) {

                    showEmptyState()

                } else {

                    layoutRepairHistoryEmpty.visibility = View.GONE
                    recyclerRepairHistory.visibility = View.VISIBLE

                    repairAdapter.updateData(repairs)
                }
            },

            onFailure = { exception ->

                progressRepairHistory.visibility = View.GONE
                recyclerRepairHistory.visibility = View.GONE

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
}