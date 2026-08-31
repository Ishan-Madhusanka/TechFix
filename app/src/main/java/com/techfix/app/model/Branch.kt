package com.techfix.app.model

data class Branch(
    var id: String = "",
    var name: String = "",
    var city: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isActive: Boolean = true
)