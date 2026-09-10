package com.techfix.app.technician

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.techfix.app.R
import com.techfix.app.model.Payment
import com.techfix.app.model.RepairRequest
import com.techfix.app.repository.PaymentRepository
import com.techfix.app.repository.RepairRequestRepository
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var txtPaymentRepairId: TextView
    private lateinit var txtPaymentDevice: TextView
    private lateinit var txtRepairPrice: TextView
    private lateinit var editPaymentAmount: TextInputEditText
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var btnRecordPayment: MaterialButton

    private val repairRepository = RepairRequestRepository()
    private val paymentRepository = PaymentRepository()

    private var repairId: String = ""
    private var technicianId: String = ""
    private var currentRepair: RepairRequest? = null

    private val paymentMethods = listOf(
        "Cash",
        "Card"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initializeViews()
        setupPaymentMethodSpinner()

        repairId = intent.getStringExtra("repairId") ?: ""
        technicianId = intent.getStringExtra("technicianId") ?: ""

        if (repairId.isEmpty()) {
            Toast.makeText(
                this,
                "Repair ID not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadRepairDetails()

        btnRecordPayment.setOnClickListener {
            recordPayment()
        }
    }

    private fun initializeViews() {

        txtPaymentRepairId =
            findViewById(R.id.txtPaymentRepairId)

        txtPaymentDevice =
            findViewById(R.id.txtPaymentDevice)

        txtRepairPrice =
            findViewById(R.id.txtRepairPrice)

        editPaymentAmount =
            findViewById(R.id.editPaymentAmount)

        spinnerPaymentMethod =
            findViewById(R.id.spinnerPaymentMethod)

        btnRecordPayment =
            findViewById(R.id.btnRecordPayment)
    }

    private fun setupPaymentMethodSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            paymentMethods
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerPaymentMethod.adapter = adapter
    }

    private fun loadRepairDetails() {

        btnRecordPayment.isEnabled = false

        repairRepository.getRepairById(
            repairId = repairId,

            onSuccess = { repair ->

                if (repair == null) {
                    Toast.makeText(
                        this,
                        "Repair not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@getRepairById
                }

                currentRepair = repair

                txtPaymentRepairId.text =
                    "Repair ID: ${repair.id}"

                txtPaymentDevice.text =
                    "Device: ${repair.deviceBrand} ${repair.deviceModel}"

                val formatter =
                    NumberFormat.getNumberInstance(Locale.US)

                txtRepairPrice.text =
                    "Repair Price: LKR ${formatter.format(repair.price)}"

                editPaymentAmount.setText(
                    repair.price.toString()
                )

                btnRecordPayment.isEnabled = true
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to load repair: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

     private fun recordPayment() {

        val repair = currentRepair

        if (repair == null) {
            Toast.makeText(
                this,
                "Repair details not loaded",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val amountText =
            editPaymentAmount.text?.toString()?.trim() ?: ""

        val amount =
            amountText.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            editPaymentAmount.error =
                "Enter a valid amount"
            return
        }

        val paymentMethod =
            spinnerPaymentMethod.selectedItem.toString()

        btnRecordPayment.isEnabled = false

        paymentRepository.getPaymentByRepairId(
            repairId = repair.id,

            onSuccess = { existingPayment ->

                if (existingPayment != null) {

                    btnRecordPayment.isEnabled = true

                    Toast.makeText(
                        this,
                        "Payment already recorded for this repair",
                        Toast.LENGTH_LONG
                    ).show()

                    return@getPaymentByRepairId
                }

                val payment = Payment(
                    repairId = repair.id,
                    customerId = repair.customerId,
                    technicianId = technicianId,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    status = "PAID",
                    paidAt = System.currentTimeMillis()
                )

                paymentRepository.recordPayment(
                    payment = payment,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Payment recorded successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    },

                    onFailure = { exception ->

                        btnRecordPayment.isEnabled = true

                        Toast.makeText(
                            this,
                            "Failed to record payment: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },

            onFailure = { exception ->

                btnRecordPayment.isEnabled = true

                Toast.makeText(
                    this,
                    "Failed to check payment: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}