package com.techfix.app.technician

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.techfix.app.R
import com.techfix.app.adapter.SparePartTechnicianAdapter
import com.techfix.app.model.SparePart
import com.techfix.app.model.UsedSparePart
import com.techfix.app.repository.RepairRequestRepository
import com.techfix.app.repository.SparePartRepository

class SparePartsActivity : AppCompatActivity() {

    private lateinit var txtSparePartsBranch: TextView
    private lateinit var recyclerSpareParts: RecyclerView
    private lateinit var progressSpareParts: ProgressBar
    private lateinit var layoutSparePartsEmpty: MaterialCardView
    private lateinit var sparePartAdapter: SparePartTechnicianAdapter

    private val sparePartRepository =
        SparePartRepository()

    private val repairRepository =
        RepairRequestRepository()

    private var branchId: String = ""
    private var repairId: String = ""
    private var technicianId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_spare_parts
        )

        initializeViews()
        setupRecyclerView()

        branchId =
            intent.getStringExtra("branchId") ?: ""

        repairId =
            intent.getStringExtra("repairId") ?: ""

        technicianId =
            intent.getStringExtra("technicianId") ?: ""

        if (branchId.isEmpty()) {

            showEmptyState()

            Toast.makeText(
                this,
                "Branch ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        txtSparePartsBranch.text =
            if (repairId.isNotEmpty()) {

                "Branch: $branchId\nRepair ID: $repairId"

            } else {

                "Branch: $branchId"
            }

        loadSpareParts()
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private fun initializeViews() {

        txtSparePartsBranch =
            findViewById(
                R.id.txtSparePartsBranch
            )

        recyclerSpareParts =
            findViewById(
                R.id.recyclerSpareParts
            )

        progressSpareParts =
            findViewById(
                R.id.progressSpareParts
            )

        layoutSparePartsEmpty =
            findViewById(
                R.id.layoutSparePartsEmpty
            )
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun setupRecyclerView() {

        sparePartAdapter =
            SparePartTechnicianAdapter(
                emptyList()
            ) { sparePart ->

                showUsePartDialog(
                    sparePart
                )
            }

        recyclerSpareParts.layoutManager =
            LinearLayoutManager(this)

        recyclerSpareParts.adapter =
            sparePartAdapter
    }

    // =========================================================
    // LOAD SPARE PARTS
    // =========================================================

    private fun loadSpareParts() {

        showLoading()

        sparePartRepository
            .getAvailableSparePartsByBranch(

                branchId = branchId,

                onSuccess = { spareParts ->

                    progressSpareParts.visibility =
                        View.GONE

                    if (spareParts.isEmpty()) {

                        showEmptyState()

                    } else {

                        layoutSparePartsEmpty.visibility =
                            View.GONE

                        recyclerSpareParts.visibility =
                            View.VISIBLE

                        sparePartAdapter.updateData(
                            spareParts
                        )
                    }
                },

                onFailure = { exception ->

                    progressSpareParts.visibility =
                        View.GONE

                    recyclerSpareParts.visibility =
                        View.GONE

                    layoutSparePartsEmpty.visibility =
                        View.VISIBLE

                    Toast.makeText(
                        this,
                        "Failed to load spare parts: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
    }

    // =========================================================
    // CUSTOM DARK USE PART DIALOG
    // =========================================================

    private fun showUsePartDialog(
        sparePart: SparePart
    ) {

        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        dialog.setContentView(
            R.layout.dialog_use_spare_part
        )

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        // -----------------------------------------------------
        // Dialog Views
        // -----------------------------------------------------

        val txtPartName =
            dialog.findViewById<TextView>(
                R.id.txtDialogPartName
            )

        val txtAvailableQuantity =
            dialog.findViewById<TextView>(
                R.id.txtDialogAvailableQuantity
            )

        val txtRepairId =
            dialog.findViewById<TextView>(
                R.id.txtDialogRepairId
            )

        val layoutRepairId =
            dialog.findViewById<View>(
                R.id.layoutDialogRepairId
            )

        val editQuantity =
            dialog.findViewById<TextInputEditText>(
                R.id.editDialogQuantity
            )

        val btnCancel =
            dialog.findViewById<MaterialButton>(
                R.id.btnDialogCancel
            )

        val btnUse =
            dialog.findViewById<MaterialButton>(
                R.id.btnDialogUse
            )

        // -----------------------------------------------------
        // Set Spare Part Information
        // -----------------------------------------------------

        txtPartName.text =
            sparePart.name

        txtAvailableQuantity.text =
            sparePart.quantity.toString()

        if (repairId.isNotEmpty()) {

            layoutRepairId.visibility =
                View.VISIBLE

            txtRepairId.text =
                repairId

        } else {

            layoutRepairId.visibility =
                View.GONE
        }

        // -----------------------------------------------------
        // Cancel Button
        // -----------------------------------------------------

        btnCancel.setOnClickListener {

            dialog.dismiss()
        }

        // -----------------------------------------------------
        // Use Part Button
        // -----------------------------------------------------

        btnUse.setOnClickListener {

            val quantityText =
                editQuantity.text
                    ?.toString()
                    ?.trim()
                    ?: ""

            // Empty quantity
            if (quantityText.isEmpty()) {

                editQuantity.error =
                    "Enter quantity"

                return@setOnClickListener
            }

            val usedQuantity =
                quantityText.toLongOrNull()

            // Invalid quantity
            if (
                usedQuantity == null ||
                usedQuantity <= 0
            ) {

                editQuantity.error =
                    "Enter a valid quantity"

                return@setOnClickListener
            }

            // Quantity greater than available stock
            if (
                usedQuantity >
                sparePart.quantity
            ) {

                editQuantity.error =
                    "Only ${sparePart.quantity} available"

                return@setOnClickListener
            }

            // Valid quantity
            dialog.dismiss()

            useSparePart(
                sparePart = sparePart,
                usedQuantity = usedQuantity
            )
        }

        // -----------------------------------------------------
        // Show Dialog
        // -----------------------------------------------------

        dialog.show()

        // Dialog width = 90% of screen
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90)
                .toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    // =========================================================
    // USE SPARE PART
    // =========================================================

    private fun useSparePart(
        sparePart: SparePart,
        usedQuantity: Long
    ) {

        sparePartRepository
            .reduceSparePartQuantity(

                sparePartId = sparePart.id,
                usedQuantity = usedQuantity,

                onSuccess = {

                    // If this screen was opened without a repair
                    if (repairId.isEmpty()) {

                        Toast.makeText(
                            this,
                            "Spare part quantity updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadSpareParts()

                        return@reduceSparePartQuantity
                    }

                    // Create used spare part record
                    val usedSparePart =
                        UsedSparePart(

                            sparePartId =
                                sparePart.id,

                            name =
                                sparePart.name,

                            quantity =
                                usedQuantity,

                            unitPrice =
                                sparePart.price
                        )

                    // Save used part inside repair request
                    repairRepository
                        .addUsedSparePart(

                            repairId =
                                repairId,

                            usedSparePart =
                                usedSparePart,

                            onSuccess = {

                                Toast.makeText(
                                    this,
                                    "${sparePart.name} added to repair",
                                    Toast.LENGTH_SHORT
                                ).show()

                                loadSpareParts()
                            },

                            onFailure = { exception ->

                                Toast.makeText(
                                    this,
                                    "Stock updated, but failed to save repair usage: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()

                                loadSpareParts()
                            }
                        )
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

    // =========================================================
    // LOADING STATE
    // =========================================================

    private fun showLoading() {

        progressSpareParts.visibility =
            View.VISIBLE

        recyclerSpareParts.visibility =
            View.GONE

        layoutSparePartsEmpty.visibility =
            View.GONE
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private fun showEmptyState() {

        progressSpareParts.visibility =
            View.GONE

        recyclerSpareParts.visibility =
            View.GONE

        layoutSparePartsEmpty.visibility =
            View.VISIBLE
    }
}
