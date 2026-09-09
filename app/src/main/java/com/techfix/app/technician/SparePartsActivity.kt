package com.techfix.app.technician

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.techfix.app.R
import com.techfix.app.adapter.SparePartTechnicianAdapter
import com.techfix.app.model.SparePart
import com.techfix.app.repository.SparePartRepository

class SparePartsActivity : AppCompatActivity() {

    private lateinit var txtSparePartsBranch: TextView
    private lateinit var recyclerSpareParts: RecyclerView
    private lateinit var progressSpareParts: ProgressBar
    private lateinit var layoutSparePartsEmpty: LinearLayout
    private lateinit var sparePartAdapter: SparePartTechnicianAdapter

    private val sparePartRepository = SparePartRepository()

    private var branchId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spare_parts)

        initializeViews()
        setupRecyclerView()

        branchId = intent.getStringExtra("branchId") ?: ""

        if (branchId.isEmpty()) {
            showEmptyState()

            Toast.makeText(
                this,
                "Branch ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        txtSparePartsBranch.text = "Branch: $branchId"

        loadSpareParts()
    }

    private fun initializeViews() {

        txtSparePartsBranch =
            findViewById(R.id.txtSparePartsBranch)

        recyclerSpareParts =
            findViewById(R.id.recyclerSpareParts)

        progressSpareParts =
            findViewById(R.id.progressSpareParts)

        layoutSparePartsEmpty =
            findViewById(R.id.layoutSparePartsEmpty)
    }

    private fun setupRecyclerView() {

        sparePartAdapter =
            SparePartTechnicianAdapter(emptyList()) { sparePart ->

                showUsePartDialog(sparePart)
            }

        recyclerSpareParts.layoutManager =
            LinearLayoutManager(this)

        recyclerSpareParts.adapter =
            sparePartAdapter
    }

    private fun loadSpareParts() {

        showLoading()

        sparePartRepository.getAvailableSparePartsByBranch(
            branchId = branchId,

            onSuccess = { spareParts ->

                progressSpareParts.visibility = View.GONE

                if (spareParts.isEmpty()) {

                    showEmptyState()

                } else {

                    layoutSparePartsEmpty.visibility = View.GONE
                    recyclerSpareParts.visibility = View.VISIBLE

                    sparePartAdapter.updateData(spareParts)
                }
            },

            onFailure = { exception ->

                progressSpareParts.visibility = View.GONE
                recyclerSpareParts.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Failed to load spare parts: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showUsePartDialog(sparePart: SparePart) {

        val input = EditText(this)

        input.hint = "Enter quantity"
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setPadding(40, 20, 40, 20)

        MaterialAlertDialogBuilder(this)
            .setTitle("Use Spare Part")
            .setMessage(
                "${sparePart.name}\nAvailable quantity: ${sparePart.quantity}"
            )
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Use") { _, _ ->

                val quantityText =
                    input.text.toString().trim()

                if (quantityText.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter quantity",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val usedQuantity =
                    quantityText.toLongOrNull()

                if (usedQuantity == null || usedQuantity <= 0) {

                    Toast.makeText(
                        this,
                        "Enter a valid quantity",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (usedQuantity > sparePart.quantity) {

                    Toast.makeText(
                        this,
                        "Not enough quantity available",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                reduceSparePartQuantity(
                    sparePart = sparePart,
                    usedQuantity = usedQuantity
                )
            }
            .show()
    }

    private fun reduceSparePartQuantity(
        sparePart: SparePart,
        usedQuantity: Long
    ) {

        sparePartRepository.reduceSparePartQuantity(
            sparePartId = sparePart.id,
            usedQuantity = usedQuantity,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Spare part quantity updated",
                    Toast.LENGTH_SHORT
                ).show()

                loadSpareParts()
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to update quantity: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading() {

        progressSpareParts.visibility = View.VISIBLE
        recyclerSpareParts.visibility = View.GONE
        layoutSparePartsEmpty.visibility = View.GONE
    }

    private fun showEmptyState() {

        progressSpareParts.visibility = View.GONE
        recyclerSpareParts.visibility = View.GONE
        layoutSparePartsEmpty.visibility = View.VISIBLE
    }
}