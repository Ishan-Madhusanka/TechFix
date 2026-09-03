package com.techfix.app.admin

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techfix.app.R
import com.techfix.app.adapter.BranchAdapter
import com.techfix.app.repository.BranchRepository

class ManageBranchesActivity : AppCompatActivity() {

    private lateinit var recyclerBranches: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyMessage: TextView

    private lateinit var branchAdapter: BranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_branches)

        recyclerBranches = findViewById(R.id.recyclerBranches)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        branchAdapter = BranchAdapter(emptyList())

        recyclerBranches.layoutManager =
            LinearLayoutManager(this)

        recyclerBranches.adapter =
            branchAdapter

        loadBranches()
    }

    private fun loadBranches() {

        progressBar.visibility = View.VISIBLE
        tvEmptyMessage.visibility = View.GONE

        val branchRepository =
            BranchRepository()

        branchRepository.getBranches(

            onSuccess = { branches ->

                progressBar.visibility = View.GONE

                if (branches.isEmpty()) {

                    recyclerBranches.visibility =
                        View.GONE

                    tvEmptyMessage.visibility =
                        View.VISIBLE

                    tvEmptyMessage.text =
                        "No branches found"

                } else {

                    recyclerBranches.visibility =
                        View.VISIBLE

                    tvEmptyMessage.visibility =
                        View.GONE

                    branchAdapter.updateBranches(
                        branches
                    )
                }
            },

            onFailure = { exception ->

                progressBar.visibility =
                    View.GONE

                recyclerBranches.visibility =
                    View.GONE

                tvEmptyMessage.visibility =
                    View.VISIBLE

                tvEmptyMessage.text =
                    "Failed to load branches:\n${exception.message}"
            }
        )
    }
}