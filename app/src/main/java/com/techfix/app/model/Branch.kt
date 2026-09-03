package com.techfix.app.model

import com.google.firebase.firestore.PropertyName

data class Branch(

    var id: String = "",

    var name: String = "",

    var city: String = "",

    var latitude: Double = 0.0,

    var longitude: Double = 0.0,

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true
)