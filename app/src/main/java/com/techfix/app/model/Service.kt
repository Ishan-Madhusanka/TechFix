package com.techfix.app.model

import com.google.firebase.firestore.PropertyName

data class Service(

    var id: String = "",

    var name: String = "",

    var categoryId: String = "",

    var duration: String = "",

    var price: Double = 0.0,

    var requiredPartId: String = "",

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true

)