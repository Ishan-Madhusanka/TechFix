package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techfix.app.R
import com.techfix.app.model.Branch

class BranchAdapter(
    private var branchList: List<Branch>
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

        holder.tvBranchStatus.text =
            if (branch.isActive) {
                "Active"
            } else {
                "Inactive"
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