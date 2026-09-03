package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.Technician

class TechnicianRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAvailableTechniciansByBranch(
        branchId: String,
        onSuccess: (List<Technician>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("technicians")
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->

                val technicians = result.documents.mapNotNull { document ->

                    document.toObject(Technician::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(technicians)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}