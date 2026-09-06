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

class MyRepairsActivity : AppCompatActivity() {

    private lateinit var recyclerRepairs: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    private lateinit var repairAdapter: RepairAdapter
    private val repairRepository = RepairRequestRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_repairs)

        initializeViews()
        setupRecyclerView()
        loadRepairs()
    }

    private fun initializeViews() {
        recyclerRepairs = findViewById(R.id.recyclerRepairs)
        progressBar = findViewById(R.id.progressBar)
        layoutEmpty = findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {

        repairAdapter = RepairAdapter(
            emptyList()
        ) { repairRequest ->

            Toast.makeText(
                this,
                "Repair: ${repairRequest.id}",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerRepairs.layoutManager = LinearLayoutManager(this)
        recyclerRepairs.adapter = repairAdapter
    }

    private fun loadRepairs() {

        val customerId = intent.getStringExtra("customerId")

        if (customerId.isNullOrEmpty()) {
            showEmptyState()
            return
        }

        showLoading()

        repairRepository.getRepairsByCustomer(
            customerId = customerId,

            onSuccess = { repairs ->

                progressBar.visibility = View.GONE

                if (repairs.isEmpty()) {
                    showEmptyState()
                } else {
                    layoutEmpty.visibility = View.GONE
                    recyclerRepairs.visibility = View.VISIBLE

                    repairAdapter.updateData(repairs)
                }
            },

            onFailure = { exception ->

                progressBar.visibility = View.GONE
                recyclerRepairs.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Failed to load repairs: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerRepairs.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        recyclerRepairs.visibility = View.GONE
        layoutEmpty.visibility = View.VISIBLE
    }
}