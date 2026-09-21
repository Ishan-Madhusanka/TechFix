package com.techfix.app.repair

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.techfix.app.R
import com.techfix.app.model.RepairStatus
import com.techfix.app.repository.RepairRequestRepository

class RepairTrackingActivity : AppCompatActivity() {

    private lateinit var txtTrackingRepairId: TextView
    private lateinit var txtCurrentStatus: TextView

    private lateinit var statusPending: TextView
    private lateinit var statusConfirmed: TextView
    private lateinit var statusDeviceReceived: TextView
    private lateinit var statusDiagnosing: TextView
    private lateinit var statusRepairing: TextView
    private lateinit var statusQualityCheck: TextView
    private lateinit var statusReadyCollection: TextView
    private lateinit var statusCompleted: TextView

    private val repairRepository = RepairRequestRepository()

    private var repairId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair_tracking)

        initializeViews()

        repairId = intent.getStringExtra("repairId") ?: ""

        if (repairId.isEmpty()) {
            Toast.makeText(
                this,
                "Repair ID not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadRepairTracking()
    }

    private fun initializeViews() {
        txtTrackingRepairId = findViewById(R.id.txtTrackingRepairId)
        txtCurrentStatus = findViewById(R.id.txtCurrentStatus)

        statusPending = findViewById(R.id.statusPending)
        statusConfirmed = findViewById(R.id.statusConfirmed)
        statusDeviceReceived = findViewById(R.id.statusDeviceReceived)
        statusDiagnosing = findViewById(R.id.statusDiagnosing)
        statusRepairing = findViewById(R.id.statusRepairing)
        statusQualityCheck = findViewById(R.id.statusQualityCheck)
        statusReadyCollection = findViewById(R.id.statusReadyCollection)
        statusCompleted = findViewById(R.id.statusCompleted)
    }

    private fun loadRepairTracking() {

        repairRepository.getRepairById(
            repairId = repairId,

            onSuccess = { repair ->

                if (repair == null) {
                    Toast.makeText(
                        this,
                        "Repair request not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@getRepairById
                }

                txtTrackingRepairId.text = "Repair #${repair.id}"

                txtCurrentStatus.text =
                    repair.status.replace("_", " ")

                updateTrackingUI(repair.status)
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to load tracking: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun updateTrackingUI(currentStatus: String) {

        val statuses = listOf(
            RepairStatus.PENDING,
            RepairStatus.CONFIRMED,
            RepairStatus.DEVICE_RECEIVED,
            RepairStatus.DIAGNOSING,
            RepairStatus.REPAIRING,
            RepairStatus.QUALITY_CHECK,
            RepairStatus.READY_FOR_COLLECTION,
            RepairStatus.COMPLETED
        )

        val statusViews = listOf(
            statusPending,
            statusConfirmed,
            statusDeviceReceived,
            statusDiagnosing,
            statusRepairing,
            statusQualityCheck,
            statusReadyCollection,
            statusCompleted
        )

        val currentIndex =
            statuses.indexOfFirst {
                it.name.equals(
                    currentStatus,
                    ignoreCase = true
                )
            }

        statusViews.forEachIndexed { index, textView ->

            val label = statuses[index]
                .name
                .replace("_", " ")
                .lowercase()
                .split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar {
                        it.uppercase()
                    }
                }

            when {
                index < currentIndex -> {
                    textView.text = "✓  $label"
                    textView.setTextColor(
                        getColor(R.color.techfix_blue)
                    )
                }

                index == currentIndex -> {
                    textView.text = "●  $label"
                    textView.setTextColor(
                        getColor(R.color.techfix_blue)
                    )
                }

                else -> {
                    textView.text = "○  $label"
                    textView.setTextColor(
                        getColor(R.color.techfix_gray)
                    )
                }
            }
        }
    }
}