package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.Technician

class TechnicianAdminAdapter(
    private var technicianList: List<Technician>,
    private val onEditClick: (Technician) -> Unit,
    private val onToggleClick: (Technician) -> Unit
) : RecyclerView.Adapter<TechnicianAdminAdapter.TechnicianViewHolder>() {

    class TechnicianViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvTechnicianName: TextView =
            itemView.findViewById(R.id.tvTechnicianName)

        val tvTechnicianSpeciality: TextView =
            itemView.findViewById(R.id.tvTechnicianSpeciality)

        val tvTechnicianBranch: TextView =
            itemView.findViewById(R.id.tvTechnicianBranch)

        val tvTechnicianPhone: TextView =
            itemView.findViewById(R.id.tvTechnicianPhone)

        val tvTechnicianAvailability: TextView =
            itemView.findViewById(R.id.tvTechnicianAvailability)

        val tvTechnicianStatus: TextView =
            itemView.findViewById(R.id.tvTechnicianStatus)

        val btnEditTechnician: MaterialButton =
            itemView.findViewById(R.id.btnEditTechnician)

        val btnToggleTechnician: MaterialButton =
            itemView.findViewById(R.id.btnToggleTechnician)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TechnicianViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_technician_admin, parent, false)

        return TechnicianViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TechnicianViewHolder,
        position: Int
    ) {
        val technician = technicianList[position]

        holder.tvTechnicianName.text = technician.name

        holder.tvTechnicianSpeciality.text =
            "Speciality: ${technician.speciality}"

        holder.tvTechnicianBranch.text =
            "Branch: ${technician.branchId}"

        holder.tvTechnicianPhone.text =
            "Phone: ${technician.phone}"

        if (technician.isAvailable) {
            holder.tvTechnicianAvailability.text = "Available"
        } else {
            holder.tvTechnicianAvailability.text = "Unavailable"
        }

        if (technician.isActive) {
            holder.tvTechnicianStatus.text = "Active"
            holder.btnToggleTechnician.text = "Deactivate"
        } else {
            holder.tvTechnicianStatus.text = "Inactive"
            holder.btnToggleTechnician.text = "Activate"
        }

        holder.btnEditTechnician.setOnClickListener {
            onEditClick(technician)
        }

        holder.btnToggleTechnician.setOnClickListener {
            onToggleClick(technician)
        }
    }

    override fun getItemCount(): Int {
        return technicianList.size
    }

    fun updateTechnicians(newTechnicians: List<Technician>) {
        technicianList = newTechnicians
        notifyDataSetChanged()
    }
}