package com.techfix.app.model

data class Payment(
    var id: String = "",
    var repairId: String = "",
    var customerId: String = "",
    var technicianId: String = "",
    var amount: Double = 0.0,
    var paymentMethod: String = "",
    var status: String = "PAID",
    var paidAt: Long = System.currentTimeMillis()
)