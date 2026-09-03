package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.Branch

class BranchAdapter(
    private var branchList: List<Branch>,
    private val onEditClick: (Branch) -> Unit,
    private val onToggleClick: (Branch) -> Unit
) : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    class BranchViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvBranchName: TextView =
            itemView.findViewById(R.id.tvBranchName)

        val tvBranchCity: TextView =
            itemView.findViewById(R.id.tvBranchCity)

        val tvBranchLocation: TextView =
            itemView.findViewById(R.id.tvBranchLocation)

        val tvBranchStatus: TextView =
            itemView.findViewById(R.id.tvBranchStatus)

        val btnEditBranch: MaterialButton =
            itemView.findViewById(R.id.btnEditBranch)

        val btnToggleBranch: MaterialButton =
            itemView.findViewById(R.id.btnToggleBranch)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BranchViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_branch,
                parent,
                false
            )

        return BranchViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BranchViewHolder,
        position: Int
    ) {

        val branch = branchList[position]

        holder.tvBranchName.text =
            branch.name

        holder.tvBranchCity.text =
            "City: ${branch.city}"

        holder.tvBranchLocation.text =
            "Location: ${branch.latitude}, ${branch.longitude}"

        if (branch.isActive) {

            holder.tvBranchStatus.text = "Active"
            holder.btnToggleBranch.text = "Deactivate"

        } else {

            holder.tvBranchStatus.text = "Inactive"
            holder.btnToggleBranch.text = "Activate"
        }

        holder.btnEditBranch.setOnClickListener {
            onEditClick(branch)
        }

        holder.btnToggleBranch.setOnClickListener {
            onToggleClick(branch)
        }
    }

    override fun getItemCount(): Int {
        return branchList.size
    }

    fun updateBranches(
        newBranches: List<Branch>
    ) {

        branchList = newBranches

        notifyDataSetChanged()
    }
}