package com.techfix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.Service

class ServiceAdminAdapter(
    private var serviceList: List<Service>,
    private val onEditClick: (Service) -> Unit,
    private val onToggleClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdminAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServiceName: TextView =
            itemView.findViewById(R.id.tvServiceName)

        val tvServiceCategory: TextView =
            itemView.findViewById(R.id.tvServiceCategory)

        val tvServicePrice: TextView =
            itemView.findViewById(R.id.tvServicePrice)

        val tvServiceDescription: TextView =
            itemView.findViewById(R.id.tvServiceDescription)

        val tvServiceStatus: TextView =
            itemView.findViewById(R.id.tvServiceStatus)

        val btnEditService: MaterialButton =
            itemView.findViewById(R.id.btnEditService)

        val btnToggleService: MaterialButton =
            itemView.findViewById(R.id.btnToggleService)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_admin, parent, false)

        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int
    ) {
        val service = serviceList[position]

        holder.tvServiceName.text = service.name

        holder.tvServiceCategory.text =
            "Category: ${service.categoryId}"

        holder.tvServicePrice.text =
            "Price: Rs. %.2f".format(service.price)

        holder.tvServiceDescription.text =
            "Duration: ${service.duration}\nRequired Part: ${service.requiredPartId}"

        if (service.isActive) {
            holder.tvServiceStatus.text = "Active"
            holder.btnToggleService.text = "Deactivate"
        } else {
            holder.tvServiceStatus.text = "Inactive"
            holder.btnToggleService.text = "Activate"
        }

        holder.btnEditService.setOnClickListener {
            onEditClick(service)
        }

        holder.btnToggleService.setOnClickListener {
            onToggleClick(service)
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    fun updateServices(newServices: List<Service>) {
        serviceList = newServices
        notifyDataSetChanged()
    }
}