package com.techfix.app.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.branch.BranchMapActivity

class AdminDashboardActivity : AppCompatActivity() {

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
    }
}