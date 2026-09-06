package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import com.techfix.app.R
import com.techfix.app.model.RepairRequest
import java.text.NumberFormat
import java.util.Locale

class RepairAdapter(
    private var repairList: List<RepairRequest>,
    private val onViewDetailsClick: (RepairRequest) -> Unit
) : RecyclerView.Adapter<RepairAdapter.RepairViewHolder>() {

    class RepairViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtRepairId: TextView =
            itemView.findViewById(R.id.txtRepairId)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)

        val txtDevice: TextView =
            itemView.findViewById(R.id.txtDevice)

        val txtAppointmentDate: TextView =
            itemView.findViewById(R.id.txtAppointmentDate)

        val txtPrice: TextView =
            itemView.findViewById(R.id.txtPrice)

        val btnViewDetails: MaterialButton =
            itemView.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepairViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_repair, parent, false)

        return RepairViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RepairViewHolder,
        position: Int
    ) {
        val repair = repairList[position]

        holder.txtRepairId.text = "Repair #${repair.id}"

        holder.txtStatus.text =
            repair.status.replace("_", " ")

        holder.txtDevice.text =
            "${repair.deviceBrand} ${repair.deviceModel}"

        holder.txtAppointmentDate.text =
            "Appointment: ${repair.appointmentDate}"

        val formatter = NumberFormat.getNumberInstance(Locale.US)

        holder.txtPrice.text =
            "LKR ${formatter.format(repair.price)}"

        holder.btnViewDetails.setOnClickListener {
            onViewDetailsClick(repair)
        }

        holder.itemView.setOnClickListener {
            onViewDetailsClick(repair)
        }
    }

    override fun getItemCount(): Int {
        return repairList.size
    }

    fun updateData(newRepairList: List<RepairRequest>) {
        repairList = newRepairList
        notifyDataSetChanged()
    }
}