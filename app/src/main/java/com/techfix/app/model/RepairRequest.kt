package com.techfix.app.model

data class RepairRequest(
    var id: String = "",
    var customerId: String = "",
    var categoryId: String = "",
    var deviceBrand: String = "",
    var deviceModel: String = "",
    var serviceId: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var branchId: String = "",
    var technicianId: String = "",
    var appointmentDate: String = "",
    var status: String = RepairStatus.PENDING.name,
    var price: Double = 0.0,
    var repairNotes: String = "",
    var createdAt: Long = System.currentTimeMillis()
)