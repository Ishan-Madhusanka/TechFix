package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.RepairRequest
import com.techfix.app.model.RepairStatus

class RepairRequestRepository {

    private val db = FirebaseFirestore.getInstance()
    private val repairRequestsCollection = db.collection("repairRequests")

    fun createRepairRequest(
        repairRequest: RepairRequest,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val documentReference = repairRequestsCollection.document()

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

    fun getRepairsByCustomer(
        customerId: String,
        onSuccess: (List<RepairRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { result ->

                val repairList = result.documents.mapNotNull { document ->
                    document.toObject(RepairRequest::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(repairList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getRepairsByTechnician(
        technicianId: String,
        onSuccess: (List<RepairRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .whereEqualTo("technicianId", technicianId)
            .get()
            .addOnSuccessListener { result ->

                val repairList = result.documents.mapNotNull { document ->
                    document.toObject(RepairRequest::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(repairList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getRepairById(
        repairId: String,
        onSuccess: (RepairRequest?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .document(repairId)
            .get()
            .addOnSuccessListener { document ->

                val repairRequest =
                    document.toObject(RepairRequest::class.java)?.apply {
                        id = document.id
                    }

                onSuccess(repairRequest)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateRepairStatus(
        repairId: String,
        status: RepairStatus,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .document(repairId)
            .update("status", status.name)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateRepairNotes(
        repairId: String,
        repairNotes: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .document(repairId)
            .update("repairNotes", repairNotes)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getRepairHistory(
        customerId: String,
        onSuccess: (List<RepairRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repairRequestsCollection
            .whereEqualTo("customerId", customerId)
            .whereEqualTo("status", RepairStatus.COMPLETED.name)
            .get()
            .addOnSuccessListener { result ->

                val repairHistory = result.documents.mapNotNull { document ->
                    document.toObject(RepairRequest::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(repairHistory)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}