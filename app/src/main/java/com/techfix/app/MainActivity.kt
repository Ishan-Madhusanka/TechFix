package com.techfix.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import com.techfix.app.repository.BranchRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val branchRepository = BranchRepository()

        branchRepository.getBranches(
            onSuccess = { branches ->

                branches.forEach { branch ->
                    Log.d(
                        "TECHFIX_BRANCH",
                        "${branch.name} - ${branch.city} - ${branch.latitude}, ${branch.longitude}"
                    )
                }
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_BRANCH",
                    "Firestore error: ${exception.message}"
                )
            }
        )
    }
}