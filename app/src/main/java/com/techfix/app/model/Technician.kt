package com.techfix.app.model

import com.google.firebase.firestore.PropertyName

data class Technician(

    var id: String = "",

    var name: String = "",

    var branchId: String = "",

    var phone: String = "",

    var speciality: String = "",

    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true,

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true
)