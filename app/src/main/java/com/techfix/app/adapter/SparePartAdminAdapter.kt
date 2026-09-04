package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.SparePart

class SparePartAdminAdapter(
    private var sparePartList: List<SparePart>,
    private val onEditClick: (SparePart) -> Unit,
    private val onToggleClick: (SparePart) -> Unit
) : RecyclerView.Adapter<SparePartAdminAdapter.SparePartViewHolder>() {

    class SparePartViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvSparePartName: TextView =
            itemView.findViewById(R.id.tvSparePartName)

        val tvSparePartCategory: TextView =
            itemView.findViewById(R.id.tvSparePartCategory)

        val tvSparePartBranch: TextView =
            itemView.findViewById(R.id.tvSparePartBranch)

        val tvSparePartQuantity: TextView =
            itemView.findViewById(R.id.tvSparePartQuantity)

        val tvSparePartPrice: TextView =
            itemView.findViewById(R.id.tvSparePartPrice)

        val tvSparePartAvailability: TextView =
            itemView.findViewById(R.id.tvSparePartAvailability)

        val btnEditSparePart: MaterialButton =
            itemView.findViewById(R.id.btnEditSparePart)

        val btnToggleSparePart: MaterialButton =
            itemView.findViewById(R.id.btnToggleSparePart)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SparePartViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spare_part_admin, parent, false)

        return SparePartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SparePartViewHolder,
        position: Int
    ) {

        val sparePart = sparePartList[position]

        holder.tvSparePartName.text = sparePart.name

        holder.tvSparePartCategory.text =
            "Category: ${sparePart.categoryId}"

        holder.tvSparePartBranch.text =
            "Branch: ${sparePart.branchId}"

        holder.tvSparePartQuantity.text =
            "Quantity: ${sparePart.quantity}"

        holder.tvSparePartPrice.text =
            "Price: Rs. %.2f".format(sparePart.price)

        if (sparePart.isAvailable) {

            holder.tvSparePartAvailability.text = "Available"
            holder.btnToggleSparePart.text = "Unavailable"

        } else {

            holder.tvSparePartAvailability.text = "Unavailable"
            holder.btnToggleSparePart.text = "Make Available"
        }

        holder.btnEditSparePart.setOnClickListener {
            onEditClick(sparePart)
        }

        holder.btnToggleSparePart.setOnClickListener {
            onToggleClick(sparePart)
        }
    }

    override fun getItemCount(): Int {
        return sparePartList.size
    }

    fun updateSpareParts(newSpareParts: List<SparePart>) {
        sparePartList = newSpareParts
        notifyDataSetChanged()
    }
}