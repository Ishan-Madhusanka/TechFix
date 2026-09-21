package com.techfix.app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.R
import com.techfix.app.branch.BranchMapActivity

class AdminDashboardActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var tvBranchCount: TextView
    private lateinit var tvServiceCount: TextView
    private lateinit var tvTechnicianCount: TextView
    private lateinit var tvSparePartCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // =========================
        // OVERVIEW COUNTS
        // =========================

        tvBranchCount = findViewById(R.id.tvBranchCount)
        tvServiceCount = findViewById(R.id.tvServiceCount)
        tvTechnicianCount = findViewById(R.id.tvTechnicianCount)
        tvSparePartCount = findViewById(R.id.tvSparePartCount)

        // =========================
        // OVERVIEW CARDS
        // =========================

        val cardBranches =
            findViewById<MaterialCardView>(R.id.cardBranches)

        val cardServices =
            findViewById<MaterialCardView>(R.id.cardServices)

        val cardTechnicians =
            findViewById<MaterialCardView>(R.id.cardTechnicians)

        val cardSpareParts =
            findViewById<MaterialCardView>(R.id.cardSpareParts)

        // =========================
        // MANAGEMENT BUTTONS
        // =========================

        val btnManageBranches =
            findViewById<MaterialButton>(R.id.btnManageBranches)

        val btnManageServices =
            findViewById<MaterialButton>(R.id.btnManageServices)

        val btnManageTechnicians =
            findViewById<MaterialButton>(R.id.btnManageTechnicians)

        val btnManageSpareParts =
            findViewById<MaterialButton>(R.id.btnManageSpareParts)

        val btnViewBranchMap =
            findViewById<MaterialButton>(R.id.btnViewBranchMap)

        // =========================
        // OVERVIEW CARD CLICKS
        // =========================

        // Branches Card
        cardBranches.setOnClickListener {

            val intent = Intent(
                this,
                ManageBranchesActivity::class.java
            )

            startActivity(intent)
        }

        // Services Card
        cardServices.setOnClickListener {

            val intent = Intent(
                this,
                ManageServicesActivity::class.java
            )

            startActivity(intent)
        }

        // Technicians Card
        cardTechnicians.setOnClickListener {

            val intent = Intent(
                this,
                ManageTechniciansActivity::class.java
            )

            startActivity(intent)
        }

        // Spare Parts Card
        cardSpareParts.setOnClickListener {

            val intent = Intent(
                this,
                ManageSparePartsActivity::class.java
            )

            startActivity(intent)
        }

        // =========================
        // MANAGEMENT BUTTON CLICKS
        // =========================

        // View Branch Map
        btnViewBranchMap.setOnClickListener {

            val intent = Intent(
                this,
                BranchMapActivity::class.java
            )

            startActivity(intent)
        }

        // Manage Branches
        btnManageBranches.setOnClickListener {

            val intent = Intent(
                this,
                ManageBranchesActivity::class.java
            )

            startActivity(intent)
        }

        // Manage Services
        btnManageServices.setOnClickListener {

            val intent = Intent(
                this,
                ManageServicesActivity::class.java
            )

            startActivity(intent)
        }

        // Manage Technicians
        btnManageTechnicians.setOnClickListener {

            val intent = Intent(
                this,
                ManageTechniciansActivity::class.java
            )

            startActivity(intent)
        }

        // Manage Spare Parts
        btnManageSpareParts.setOnClickListener {

            val intent = Intent(
                this,
                ManageSparePartsActivity::class.java
            )

            startActivity(intent)
        }

        // Load Firestore counts
        loadDashboardCounts(
            tvBranchCount,
            tvServiceCount,
            tvTechnicianCount,
            tvSparePartCount
        )
    }

    override fun onResume() {
        super.onResume()

        // Refresh dashboard counts when returning
        // from management screens
        if (::tvBranchCount.isInitialized) {

            loadDashboardCounts(
                tvBranchCount,
                tvServiceCount,
                tvTechnicianCount,
                tvSparePartCount
            )
        }
    }

    private fun loadDashboardCounts(
        tvBranchCount: TextView,
        tvServiceCount: TextView,
        tvTechnicianCount: TextView,
        tvSparePartCount: TextView
    ) {

        // Branch count
        db.collection("branches")
            .get()
            .addOnSuccessListener { result ->
                tvBranchCount.text = result.size().toString()
            }
            .addOnFailureListener {
                tvBranchCount.text = "0"
            }

        // Service count
        db.collection("services")
            .get()
            .addOnSuccessListener { result ->
                tvServiceCount.text = result.size().toString()
            }
            .addOnFailureListener {
                tvServiceCount.text = "0"
            }

        // Technician count
        db.collection("technicians")
            .get()
            .addOnSuccessListener { result ->
                tvTechnicianCount.text = result.size().toString()
            }
            .addOnFailureListener {
                tvTechnicianCount.text = "0"
            }

        // Spare part count
        db.collection("spareParts")
            .get()
            .addOnSuccessListener { result ->
                tvSparePartCount.text = result.size().toString()
            }
            .addOnFailureListener {
                tvSparePartCount.text = "0"
            }
    }
}