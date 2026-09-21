package com.techfix.app.model

data class UsedSparePart(
    var sparePartId: String = "",
    var name: String = "",
    var quantity: Long = 0,
    var unitPrice: Double = 0.0
)