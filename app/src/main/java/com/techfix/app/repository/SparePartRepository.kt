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
                    document.toObject(SparePart::class.java)?.apply {
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

    fun getAllSpareParts(
        onSuccess: (List<SparePart>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("spareParts")
            .get()
            .addOnSuccessListener { result ->

                val spareParts = result.documents.mapNotNull { document ->
                    document.toObject(SparePart::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(spareParts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addSparePart(
        sparePart: SparePart,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sparePartData = hashMapOf<String, Any>(
            "name" to sparePart.name,
            "categoryId" to sparePart.categoryId,
            "branchId" to sparePart.branchId,
            "quantity" to sparePart.quantity,
            "price" to sparePart.price,
            "isAvailable" to sparePart.isAvailable
        )

        db.collection("spareParts")
            .add(sparePartData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateSparePart(
        sparePart: SparePart,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (sparePart.id.isEmpty()) {
            onFailure(Exception("Spare Part ID is empty"))
            return
        }

        val sparePartData = hashMapOf<String, Any>(
            "name" to sparePart.name,
            "categoryId" to sparePart.categoryId,
            "branchId" to sparePart.branchId,
            "quantity" to sparePart.quantity,
            "price" to sparePart.price,
            "isAvailable" to sparePart.isAvailable
        )

        db.collection("spareParts")
            .document(sparePart.id)
            .update(sparePartData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateSparePartAvailability(
        sparePartId: String,
        isAvailable: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("spareParts")
            .document(sparePartId)
            .update("isAvailable", isAvailable)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getRequiredSparePartByBranch(
        branchId: String,
        requiredPartId: String,
        onSuccess: (SparePart?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("spareParts")
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->

                val requiredSparePart = result.documents
                    .mapNotNull { document ->
                        document.toObject(SparePart::class.java)?.apply {
                            id = document.id
                        }
                    }
                    .firstOrNull { sparePart ->
                        sparePart.id == requiredPartId &&
                                sparePart.quantity > 0
                    }

                onSuccess(requiredSparePart)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}