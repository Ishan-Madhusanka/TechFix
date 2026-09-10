package com.techfix.app.technician

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.techfix.app.R
import com.techfix.app.repository.TechnicianRepository

class TechnicianLoginActivity : AppCompatActivity() {

    private lateinit var layoutTechnicianPhone: TextInputLayout
    private lateinit var editTechnicianPhone: TextInputEditText
    private lateinit var btnTechnicianLogin: MaterialButton
    private lateinit var progressTechnicianLogin: ProgressBar

    private val technicianRepository =
        TechnicianRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_technician_login
        )

        initializeViews()
        setupLoginButton()
    }

    private fun initializeViews() {

        layoutTechnicianPhone =
            findViewById(
                R.id.layoutTechnicianPhone
            )

        editTechnicianPhone =
            findViewById(
                R.id.editTechnicianPhone
            )

        btnTechnicianLogin =
            findViewById(
                R.id.btnTechnicianLogin
            )

        progressTechnicianLogin =
            findViewById(
                R.id.progressTechnicianLogin
            )
    }

    private fun setupLoginButton() {

        btnTechnicianLogin.setOnClickListener {

            val phone =
                editTechnicianPhone
                    .text
                    ?.toString()
                    ?.trim()
                    ?: ""

            layoutTechnicianPhone.error = null

            if (phone.isEmpty()) {

                layoutTechnicianPhone.error =
                    "Enter your phone number"

                return@setOnClickListener
            }

            if (phone.length < 9) {

                layoutTechnicianPhone.error =
                    "Enter a valid phone number"

                return@setOnClickListener
            }

            loginTechnician(phone)
        }
    }

    private fun loginTechnician(
        phone: String
    ) {

        showLoading(true)

        technicianRepository.loginTechnician(

            phone = phone,

            onSuccess = { technician ->

                showLoading(false)

                if (technician == null) {

                    Toast.makeText(
                        this,
                        "Technician not found or account is inactive",
                        Toast.LENGTH_LONG
                    ).show()

                    return@loginTechnician
                }

                Toast.makeText(
                    this,
                    "Welcome ${technician.name}",
                    Toast.LENGTH_SHORT
                ).show()

                val dashboardIntent =
                    Intent(
                        this,
                        TechnicianDashboardActivity::class.java
                    )

                dashboardIntent.putExtra(
                    "technicianId",
                    technician.id
                )

                dashboardIntent.putExtra(
                    "branchId",
                    technician.branchId
                )

                dashboardIntent.putExtra(
                    "technicianName",
                    technician.name
                )

                startActivity(
                    dashboardIntent
                )

                finish()
            },

            onFailure = { exception ->

                showLoading(false)

                Toast.makeText(
                    this,
                    "Login failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showLoading(
        isLoading: Boolean
    ) {

        if (isLoading) {

            progressTechnicianLogin.visibility =
                View.VISIBLE

            btnTechnicianLogin.isEnabled =
                false

            editTechnicianPhone.isEnabled =
                false

        } else {

            progressTechnicianLogin.visibility =
                View.GONE

            btnTechnicianLogin.isEnabled =
                true

            editTechnicianPhone.isEnabled =
                true
        }
    }
}