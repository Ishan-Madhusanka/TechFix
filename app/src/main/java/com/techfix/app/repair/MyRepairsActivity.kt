package com.techfix.app.repair

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.adapter.RepairAdapter
import com.techfix.app.repository.RepairRequestRepository

class MyRepairsActivity : AppCompatActivity() {

    private lateinit var recyclerRepairs: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var btnRepairHistory: MaterialButton

    private lateinit var repairAdapter: RepairAdapter

    private val repairRepository =
        RepairRequestRepository()

    private var customerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_repairs)

        initializeViews()
        setupRecyclerView()

        // Customer ID is passed from the previous screen.
        // Later this can be connected with Member 2 authentication.
        customerId =
            intent.getStringExtra("customerId") ?: ""

        setupRepairHistoryButton()

        if (customerId.isEmpty()) {

            showEmptyState()

            Toast.makeText(
                this,
                "Customer ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        loadRepairs()
    }

    private fun initializeViews() {

        recyclerRepairs =
            findViewById(R.id.recyclerRepairs)

        progressBar =
            findViewById(R.id.progressBar)

        layoutEmpty =
            findViewById(R.id.layoutEmpty)

        btnRepairHistory =
            findViewById(R.id.btnRepairHistory)
    }

    private fun setupRecyclerView() {

        repairAdapter =
            RepairAdapter(emptyList()) { repair ->

                val detailsIntent =
                    Intent(
                        this,
                        RepairDetailsActivity::class.java
                    )

                detailsIntent.putExtra(
                    "repairId",
                    repair.id
                )

                startActivity(detailsIntent)
            }

        recyclerRepairs.layoutManager =
            LinearLayoutManager(this)

        recyclerRepairs.adapter =
            repairAdapter
    }

    private fun setupRepairHistoryButton() {

        btnRepairHistory.setOnClickListener {

            if (customerId.isEmpty()) {

                Toast.makeText(
                    this,
                    "Customer ID not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val historyIntent =
                Intent(
                    this,
                    RepairHistoryActivity::class.java
                )

            historyIntent.putExtra(
                "customerId",
                customerId
            )

            startActivity(historyIntent)
        }
    }

    private fun loadRepairs() {

        showLoading()

        repairRepository.getRepairsByCustomer(

            customerId = customerId,

            onSuccess = { repairs ->

                progressBar.visibility =
                    View.GONE

                if (repairs.isEmpty()) {

                    showEmptyState()

                } else {

                    layoutEmpty.visibility =
                        View.GONE

                    recyclerRepairs.visibility =
                        View.VISIBLE

                    repairAdapter.updateData(
                        repairs
                    )
                }
            },

            onFailure = { exception ->

                progressBar.visibility =
                    View.GONE

                recyclerRepairs.visibility =
                    View.GONE

                layoutEmpty.visibility =
                    View.VISIBLE

                Toast.makeText(
                    this,
                    "Failed to load repairs: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading() {

        progressBar.visibility =
            View.VISIBLE

        recyclerRepairs.visibility =
            View.GONE

        layoutEmpty.visibility =
            View.GONE
    }

    private fun showEmptyState() {

        progressBar.visibility =
            View.GONE

        recyclerRepairs.visibility =
            View.GONE

        layoutEmpty.visibility =
            View.VISIBLE
    }
}