package com.techfix.app.model

import com.google.firebase.firestore.PropertyName

data class SparePart(

    var id: String = "",

    var name: String = "",

    var categoryId: String = "",

    var branchId: String = "",

    var quantity: Long = 0,

    var price: Double = 0.0,

    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true
)