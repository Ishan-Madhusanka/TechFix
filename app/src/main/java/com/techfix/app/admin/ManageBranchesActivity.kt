package com.techfix.app.admin

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techfix.app.R
import com.techfix.app.adapter.BranchAdapter
import com.techfix.app.model.Branch
import com.techfix.app.repository.BranchRepository

class ManageBranchesActivity : AppCompatActivity() {

    private lateinit var recyclerBranches: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyMessage: TextView

    private lateinit var branchAdapter: BranchAdapter
    private val branchRepository = BranchRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_branches)

        recyclerBranches = findViewById(R.id.recyclerBranches)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        branchAdapter = BranchAdapter(
            branchList = emptyList(),

            onEditClick = { branch ->
                showEditBranchDialog(branch)
            },

            onToggleClick = { branch ->
                toggleBranchStatus(branch)
            }
        )

        recyclerBranches.layoutManager =
            LinearLayoutManager(this)

        recyclerBranches.adapter =
            branchAdapter

        loadBranches()
    }

    private fun loadBranches() {

        progressBar.visibility = View.VISIBLE
        tvEmptyMessage.visibility = View.GONE

        branchRepository.getAllBranches(

            onSuccess = { branches ->

                progressBar.visibility = View.GONE

                if (branches.isEmpty()) {

                    recyclerBranches.visibility = View.GONE
                    tvEmptyMessage.visibility = View.VISIBLE
                    tvEmptyMessage.text = "No branches found"

                } else {

                    recyclerBranches.visibility = View.VISIBLE
                    tvEmptyMessage.visibility = View.GONE

                    branchAdapter.updateBranches(branches)
                }
            },

            onFailure = { exception ->

                progressBar.visibility = View.GONE
                recyclerBranches.visibility = View.GONE
                tvEmptyMessage.visibility = View.VISIBLE

                tvEmptyMessage.text =
                    "Failed to load branches:\n${exception.message}"
            }
        )
    }

    private fun toggleBranchStatus(branch: Branch) {

        val newStatus = !branch.isActive

        val actionText =
            if (newStatus) "activate" else "deactivate"

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(
                "Are you sure you want to $actionText ${branch.name}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                branchRepository.updateBranchStatus(
                    branchId = branch.id,
                    isActive = newStatus,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Branch status updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadBranches()
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

    private fun showEditBranchDialog(branch: Branch) {

        val container = android.widget.LinearLayout(this)

        container.orientation =
            android.widget.LinearLayout.VERTICAL

        val padding = (20 * resources.displayMetrics.density).toInt()

        container.setPadding(
            padding,
            padding,
            padding,
            0
        )

        val editName = EditText(this)
        editName.hint = "Branch Name"
        editName.setText(branch.name)

        val editCity = EditText(this)
        editCity.hint = "City"
        editCity.setText(branch.city)

        val editLatitude = EditText(this)
        editLatitude.hint = "Latitude"
        editLatitude.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED

        editLatitude.setText(
            branch.latitude.toString()
        )

        val editLongitude = EditText(this)
        editLongitude.hint = "Longitude"
        editLongitude.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED

        editLongitude.setText(
            branch.longitude.toString()
        )

        container.addView(editName)
        container.addView(editCity)
        container.addView(editLatitude)
        container.addView(editLongitude)

        AlertDialog.Builder(this)
            .setTitle("Edit Branch")
            .setView(container)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()
            .apply {

                setOnShowListener {

                    getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener {

                        val name =
                            editName.text.toString().trim()

                        val city =
                            editCity.text.toString().trim()

                        val latitude =
                            editLatitude.text.toString()
                                .trim()
                                .toDoubleOrNull()

                        val longitude =
                            editLongitude.text.toString()
                                .trim()
                                .toDoubleOrNull()

                        if (
                            name.isEmpty() ||
                            city.isEmpty() ||
                            latitude == null ||
                            longitude == null
                        ) {

                            Toast.makeText(
                                this@ManageBranchesActivity,
                                "Please enter valid branch details",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setOnClickListener
                        }

                        val updatedBranch = branch.copy(
                            name = name,
                            city = city,
                            latitude = latitude,
                            longitude = longitude
                        )

                        branchRepository.updateBranch(
                            branch = updatedBranch,

                            onSuccess = {

                                Toast.makeText(
                                    this@ManageBranchesActivity,
                                    "Branch updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                dismiss()
                                loadBranches()
                            },

                            onFailure = { exception ->

                                Toast.makeText(
                                    this@ManageBranchesActivity,
                                    "Error: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }

                show()
            }
    }
}