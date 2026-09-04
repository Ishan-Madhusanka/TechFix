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
import com.techfix.app.adapter.TechnicianAdminAdapter
import com.techfix.app.model.Technician
import com.techfix.app.repository.TechnicianRepository

class ManageTechniciansActivity : AppCompatActivity() {

    private lateinit var recyclerTechnicians: RecyclerView
    private lateinit var progressBarTechnicians: ProgressBar
    private lateinit var tvEmptyTechnicians: TextView
    private lateinit var btnAddTechnician: ExtendedFloatingActionButton
    private lateinit var technicianAdapter: TechnicianAdminAdapter

    private val technicianRepository = TechnicianRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_technicians)

        recyclerTechnicians = findViewById(R.id.recyclerTechnicians)
        progressBarTechnicians = findViewById(R.id.progressBarTechnicians)
        tvEmptyTechnicians = findViewById(R.id.tvEmptyTechnicians)
        btnAddTechnician = findViewById(R.id.btnAddTechnician)

        technicianAdapter = TechnicianAdminAdapter(
            technicianList = emptyList(),

            onEditClick = { technician ->
                showTechnicianDialog(technician)
            },

            onToggleClick = { technician ->
                toggleTechnicianStatus(technician)
            }
        )

        recyclerTechnicians.layoutManager = LinearLayoutManager(this)
        recyclerTechnicians.adapter = technicianAdapter

        btnAddTechnician.setOnClickListener {
            showTechnicianDialog(null)
        }

        loadTechnicians()
    }

    private fun loadTechnicians() {

        progressBarTechnicians.visibility = View.VISIBLE
        tvEmptyTechnicians.visibility = View.GONE

        technicianRepository.getAllTechnicians(

            onSuccess = { technicians ->

                progressBarTechnicians.visibility = View.GONE

                if (technicians.isEmpty()) {

                    recyclerTechnicians.visibility = View.GONE
                    tvEmptyTechnicians.visibility = View.VISIBLE
                    tvEmptyTechnicians.text = "No technicians found"

                } else {

                    recyclerTechnicians.visibility = View.VISIBLE
                    tvEmptyTechnicians.visibility = View.GONE

                    technicianAdapter.updateTechnicians(technicians)
                }
            },

            onFailure = { exception ->

                progressBarTechnicians.visibility = View.GONE
                recyclerTechnicians.visibility = View.GONE
                tvEmptyTechnicians.visibility = View.VISIBLE

                tvEmptyTechnicians.text =
                    "Failed to load technicians:\n${exception.message}"
            }
        )
    }

    private fun showTechnicianDialog(technician: Technician?) {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_edit_technician, null)

        val editName =
            dialogView.findViewById<TextInputEditText>(
                R.id.editTechnicianName
            )

        val editSpeciality =
            dialogView.findViewById<TextInputEditText>(
                R.id.editTechnicianSpeciality
            )

        val editPhone =
            dialogView.findViewById<TextInputEditText>(
                R.id.editTechnicianPhone
            )

        val editBranch =
            dialogView.findViewById<TextInputEditText>(
                R.id.editTechnicianBranch
            )

        val switchAvailable =
            dialogView.findViewById<SwitchMaterial>(
                R.id.switchTechnicianAvailable
            )

        val btnCancel =
            dialogView.findViewById<MaterialButton>(
                R.id.btnCancelTechnician
            )

        val btnSave =
            dialogView.findViewById<MaterialButton>(
                R.id.btnSaveTechnician
            )

        // EDIT MODE
        if (technician != null) {

            editName.setText(technician.name)
            editSpeciality.setText(technician.speciality)
            editPhone.setText(technician.phone)
            editBranch.setText(technician.branchId)

            switchAvailable.isChecked =
                technician.isAvailable

            btnSave.text = "Update"

        } else {

            // ADD MODE
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

            val name =
                editName.text.toString().trim()

            val speciality =
                editSpeciality.text.toString().trim()

            val phone =
                editPhone.text.toString().trim()

            val branchId =
                editBranch.text.toString().trim()
                    .lowercase()

            val isAvailable =
                switchAvailable.isChecked

            if (
                name.isEmpty() ||
                speciality.isEmpty() ||
                phone.isEmpty() ||
                branchId.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter all technician details",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (phone.length < 9) {

                Toast.makeText(
                    this,
                    "Please enter a valid phone number",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (technician == null) {

                // ADD NEW TECHNICIAN

                val newTechnician = Technician(
                    name = name,
                    branchId = branchId,
                    speciality = speciality,
                    phone = phone,
                    isAvailable = isAvailable,
                    isActive = true
                )

                technicianRepository.addTechnician(

                    technician = newTechnician,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Technician added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadTechnicians()
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

                // UPDATE EXISTING TECHNICIAN

                val updatedTechnician =
                    technician.copy(
                        name = name,
                        branchId = branchId,
                        speciality = speciality,
                        phone = phone,
                        isAvailable = isAvailable
                    )

                technicianRepository.updateTechnician(

                    technician = updatedTechnician,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Technician updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadTechnicians()
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

    private fun toggleTechnicianStatus(
        technician: Technician
    ) {

        val newStatus = !technician.isActive

        val actionText =
            if (newStatus) "activate" else "deactivate"

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(
                "Are you sure you want to " +
                        "$actionText ${technician.name}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                technicianRepository.updateTechnicianStatus(

                    technicianId = technician.id,
                    isActive = newStatus,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Technician status updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadTechnicians()
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