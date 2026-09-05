package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.RepairRequest

class RepairRequestRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createRepairRequest(
        repairRequest: RepairRequest,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val documentReference =
            db.collection("repairRequests").document()

        repairRequest.id = documentReference.id

        documentReference
            .set(repairRequest)
            .addOnSuccessListener {
                onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}