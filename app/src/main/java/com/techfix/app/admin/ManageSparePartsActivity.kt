package com.techfix.app.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.techfix.app.R
import com.techfix.app.adapter.SparePartAdminAdapter
import com.techfix.app.model.SparePart
import com.techfix.app.repository.SparePartRepository

class ManageSparePartsActivity : AppCompatActivity() {

    private lateinit var recyclerSpareParts: RecyclerView
    private lateinit var progressBarSpareParts: ProgressBar
    private lateinit var tvEmptySpareParts: TextView
    private lateinit var btnAddSparePart: ExtendedFloatingActionButton
    private lateinit var sparePartAdapter: SparePartAdminAdapter

    private val sparePartRepository = SparePartRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_spare_parts)

        recyclerSpareParts = findViewById(R.id.recyclerSpareParts)
        progressBarSpareParts = findViewById(R.id.progressBarSpareParts)
        tvEmptySpareParts = findViewById(R.id.tvEmptySpareParts)
        btnAddSparePart = findViewById(R.id.btnAddSparePart)

        sparePartAdapter = SparePartAdminAdapter(
            sparePartList = emptyList(),
            onEditClick = { sparePart ->
                showSparePartDialog(sparePart)
            },
            onToggleClick = { sparePart ->
                toggleSparePartAvailability(sparePart)
            }
        )

        recyclerSpareParts.layoutManager = LinearLayoutManager(this)
        recyclerSpareParts.adapter = sparePartAdapter

        btnAddSparePart.setOnClickListener {
            showSparePartDialog(null)
        }

        loadSpareParts()
    }

    private fun loadSpareParts() {

        progressBarSpareParts.visibility = View.VISIBLE
        tvEmptySpareParts.visibility = View.GONE

        sparePartRepository.getAllSpareParts(
            onSuccess = { spareParts ->

                progressBarSpareParts.visibility = View.GONE

                if (spareParts.isEmpty()) {

                    recyclerSpareParts.visibility = View.GONE
                    tvEmptySpareParts.visibility = View.VISIBLE
                    tvEmptySpareParts.text = "No spare parts found"

                } else {

                    recyclerSpareParts.visibility = View.VISIBLE
                    tvEmptySpareParts.visibility = View.GONE

                    sparePartAdapter.updateSpareParts(spareParts)
                }
            },
            onFailure = { exception ->

                progressBarSpareParts.visibility = View.GONE
                recyclerSpareParts.visibility = View.GONE
                tvEmptySpareParts.visibility = View.VISIBLE

                tvEmptySpareParts.text =
                    "Failed to load spare parts:\n${exception.message}"
            }
        )
    }

    private fun showSparePartDialog(sparePart: SparePart?) {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_edit_spare_part, null)

        val editName =
            dialogView.findViewById<TextInputEditText>(R.id.editSparePartName)

        val editCategory =
            dialogView.findViewById<TextInputEditText>(R.id.editSparePartCategory)

        val editBranch =
            dialogView.findViewById<TextInputEditText>(R.id.editSparePartBranch)

        val editQuantity =
            dialogView.findViewById<TextInputEditText>(R.id.editSparePartQuantity)

        val editPrice =
            dialogView.findViewById<TextInputEditText>(R.id.editSparePartPrice)

        val switchAvailable =
            dialogView.findViewById<SwitchMaterial>(R.id.switchSparePartAvailable)

        val btnCancel =
            dialogView.findViewById<MaterialButton>(R.id.btnCancelSparePart)

        val btnSave =
            dialogView.findViewById<MaterialButton>(R.id.btnSaveSparePart)

        if (sparePart != null) {

            editName.setText(sparePart.name)
            editCategory.setText(sparePart.categoryId)
            editBranch.setText(sparePart.branchId)
            editQuantity.setText(sparePart.quantity.toString())
            editPrice.setText(sparePart.price.toString())
            switchAvailable.isChecked = sparePart.isAvailable

            btnSave.text = "Update"

        } else {

            switchAvailable.isChecked = true
            btnSave.text = "Add"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {

            val name = editName.text.toString().trim()
            val categoryId = editCategory.text.toString().trim().lowercase()
            val branchId = editBranch.text.toString().trim().lowercase()

            val quantityText = editQuantity.text.toString().trim()
            val priceText = editPrice.text.toString().trim()

            val isAvailable = switchAvailable.isChecked

            if (
                name.isEmpty() ||
                categoryId.isEmpty() ||
                branchId.isEmpty() ||
                quantityText.isEmpty() ||
                priceText.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter all spare part details",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val quantity = quantityText.toLongOrNull()
            val price = priceText.toDoubleOrNull()

            if (quantity == null || quantity < 0) {

                Toast.makeText(
                    this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (price == null || price < 0) {

                Toast.makeText(
                    this,
                    "Please enter a valid price",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (sparePart == null) {

                val newSparePart = SparePart(
                    name = name,
                    categoryId = categoryId,
                    branchId = branchId,
                    quantity = quantity,
                    price = price,
                    isAvailable = isAvailable
                )

                sparePartRepository.addSparePart(
                    sparePart = newSparePart,
                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Spare part added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadSpareParts()
                    },
                    onFailure = { exception ->

                        Toast.makeText(
                            this,
                            "Error: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )

            } else {

                val updatedSparePart = sparePart.copy(
                    name = name,
                    categoryId = categoryId,
                    branchId = branchId,
                    quantity = quantity,
                    price = price,
                    isAvailable = isAvailable
                )

                sparePartRepository.updateSparePart(
                    sparePart = updatedSparePart,
                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Spare part updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadSpareParts()
                    },
                    onFailure = { exception ->

                        Toast.makeText(
                            this,
                            "Error: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }

        dialog.show()
    }

    private fun toggleSparePartAvailability(sparePart: SparePart) {

        val newStatus = !sparePart.isAvailable

        val actionText =
            if (newStatus) "make available"
            else "make unavailable"

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(
                "Are you sure you want to $actionText ${sparePart.name}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                sparePartRepository.updateSparePartAvailability(
                    sparePartId = sparePart.id,
                    isAvailable = newStatus,
                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Spare part availability updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadSpareParts()
                    },
                    onFailure = { exception ->

                        Toast.makeText(
                            this,
                            "Error: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}