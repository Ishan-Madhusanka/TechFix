package com.techfix.app.model

data class Technician(
    var id: String = "",
    var name: String = "",
    var branchId: String = "",
    var specialization: String = "",
    var phone: String = "",
    var isAvailable: Boolean = true
)