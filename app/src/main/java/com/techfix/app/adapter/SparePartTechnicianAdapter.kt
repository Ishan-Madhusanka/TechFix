package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.SparePart
import java.text.NumberFormat
import java.util.Locale

class SparePartTechnicianAdapter(
    private var sparePartList: List<SparePart>,
    private val onUsePartClick: (SparePart) -> Unit
) : RecyclerView.Adapter<SparePartTechnicianAdapter.SparePartViewHolder>() {

    class SparePartViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtPartName: TextView =
            itemView.findViewById(R.id.txtPartName)

        val txtPartAvailability: TextView =
            itemView.findViewById(R.id.txtPartAvailability)

        val txtPartCategory: TextView =
            itemView.findViewById(R.id.txtPartCategory)

        val txtPartQuantity: TextView =
            itemView.findViewById(R.id.txtPartQuantity)

        val txtPartPrice: TextView =
            itemView.findViewById(R.id.txtPartPrice)

        val btnUsePart: MaterialButton =
            itemView.findViewById(R.id.btnUsePart)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SparePartViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_spare_part_technician,
                parent,
                false
            )

        return SparePartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SparePartViewHolder,
        position: Int
    ) {

        val sparePart = sparePartList[position]

        holder.txtPartName.text =
            sparePart.name

        holder.txtPartCategory.text =
            "Category: ${sparePart.categoryId}"

        holder.txtPartQuantity.text =
            "Quantity: ${sparePart.quantity}"

        val formatter =
            NumberFormat.getNumberInstance(Locale.US)

        holder.txtPartPrice.text =
            "LKR ${formatter.format(sparePart.price)}"

        if (sparePart.isAvailable && sparePart.quantity > 0) {

            holder.txtPartAvailability.text =
                "AVAILABLE"

            holder.btnUsePart.isEnabled = true

        } else {

            holder.txtPartAvailability.text =
                "OUT OF STOCK"

            holder.btnUsePart.isEnabled = false
        }

        holder.btnUsePart.setOnClickListener {
            onUsePartClick(sparePart)
        }
    }

    override fun getItemCount(): Int {
        return sparePartList.size
    }

    fun updateData(
        newSparePartList: List<SparePart>
    ) {
        sparePartList = newSparePartList
        notifyDataSetChanged()
    }
}