package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.SparePart

class SparePartRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAvailableSparePartsByBranch(
        branchId: String,
        onSuccess: (List<SparePart>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("spareParts")
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->

                val spareParts = result.documents.mapNotNull { document ->

                    val sparePart = document.toObject(SparePart::class.java)

                    sparePart?.apply {
                        id = document.id
                    }
                }.filter { sparePart ->
                    sparePart.quantity > 0
                }

                onSuccess(spareParts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}