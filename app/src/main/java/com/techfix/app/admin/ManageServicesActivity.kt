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
import com.google.android.material.textfield.TextInputEditText
import com.techfix.app.R
import com.techfix.app.adapter.ServiceAdminAdapter
import com.techfix.app.model.Service
import com.techfix.app.repository.ServiceRepository

class ManageServicesActivity : AppCompatActivity() {

    private lateinit var recyclerServices: RecyclerView
    private lateinit var progressBarServices: ProgressBar
    private lateinit var tvEmptyServices: TextView
    private lateinit var btnAddService: ExtendedFloatingActionButton
    private lateinit var serviceAdapter: ServiceAdminAdapter

    private val serviceRepository = ServiceRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_services)

        recyclerServices = findViewById(R.id.recyclerServices)
        progressBarServices = findViewById(R.id.progressBarServices)
        tvEmptyServices = findViewById(R.id.tvEmptyServices)
        btnAddService = findViewById(R.id.btnAddService)

        serviceAdapter = ServiceAdminAdapter(
            serviceList = emptyList(),

            onEditClick = { service ->
                showServiceDialog(service)
            },

            onToggleClick = { service ->
                toggleServiceStatus(service)
            }
        )

        recyclerServices.layoutManager = LinearLayoutManager(this)
        recyclerServices.adapter = serviceAdapter

        btnAddService.setOnClickListener {
            showServiceDialog(null)
        }

        loadServices()
    }

    private fun loadServices() {

        progressBarServices.visibility = View.VISIBLE
        tvEmptyServices.visibility = View.GONE

        serviceRepository.getAllServices(

            onSuccess = { services ->

                progressBarServices.visibility = View.GONE

                if (services.isEmpty()) {
                    recyclerServices.visibility = View.GONE
                    tvEmptyServices.visibility = View.VISIBLE
                    tvEmptyServices.text = "No services found"
                } else {
                    recyclerServices.visibility = View.VISIBLE
                    tvEmptyServices.visibility = View.GONE
                    serviceAdapter.updateServices(services)
                }
            },

            onFailure = { exception ->

                progressBarServices.visibility = View.GONE
                recyclerServices.visibility = View.GONE
                tvEmptyServices.visibility = View.VISIBLE

                tvEmptyServices.text =
                    "Failed to load services:\n${exception.message}"
            }
        )
    }

    private fun showServiceDialog(service: Service?) {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_edit_service, null)

        val editName =
            dialogView.findViewById<TextInputEditText>(R.id.editServiceName)

        val editCategory =
            dialogView.findViewById<TextInputEditText>(R.id.editServiceCategory)

        val editDuration =
            dialogView.findViewById<TextInputEditText>(R.id.editServiceDuration)

        val editPrice =
            dialogView.findViewById<TextInputEditText>(R.id.editServicePrice)

        val editRequiredPartId =
            dialogView.findViewById<TextInputEditText>(R.id.editRequiredPartId)

        val btnCancel =
            dialogView.findViewById<MaterialButton>(R.id.btnCancelService)

        val btnSave =
            dialogView.findViewById<MaterialButton>(R.id.btnSaveService)

        // Edit mode
        if (service != null) {

            editName.setText(service.name)
            editCategory.setText(service.categoryId)
            editDuration.setText(service.duration)
            editPrice.setText(service.price.toString())
            editRequiredPartId.setText(service.requiredPartId)

            btnSave.text = "Update"
        } else {

            // Add mode
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

            val category =
                editCategory.text.toString().trim()

            val duration =
                editDuration.text.toString().trim()

            val price =
                editPrice.text.toString().trim().toDoubleOrNull()

            val requiredPartId =
                editRequiredPartId.text.toString().trim()

            if (
                name.isEmpty() ||
                category.isEmpty() ||
                duration.isEmpty() ||
                price == null ||
                price < 0 ||
                requiredPartId.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter valid service details",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (service == null) {

                // ADD NEW SERVICE
                val newService = Service(
                    name = name,
                    categoryId = category,
                    duration = duration,
                    price = price,
                    requiredPartId = requiredPartId,
                    isActive = true
                )

                serviceRepository.addService(

                    service = newService,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Service added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadServices()
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

                // UPDATE EXISTING SERVICE
                val updatedService = service.copy(
                    name = name,
                    categoryId = category,
                    duration = duration,
                    price = price,
                    requiredPartId = requiredPartId
                )

                serviceRepository.updateService(

                    service = updatedService,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Service updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                        loadServices()
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

    private fun toggleServiceStatus(service: Service) {

        val newStatus = !service.isActive

        val actionText =
            if (newStatus) "activate" else "deactivate"

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(
                "Are you sure you want to $actionText ${service.name}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                serviceRepository.updateServiceStatus(

                    serviceId = service.id,
                    isActive = newStatus,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Service status updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadServices()
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