package com.techfix.app.model

import android.util.Log

data class SparePart(
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var branch: String = "",
    var quantity: Long = 0,
    var price: Double = 0.0,
    var isAvilable: Boolean = true
    )
