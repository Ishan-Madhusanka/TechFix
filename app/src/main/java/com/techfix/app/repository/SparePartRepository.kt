package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.SparePart

class SparePartRepository {

    private val db = FirebaseFirestore.getInstance()
    private val sparePartsCollection = db.collection("spareParts")


    // =========================================================
    // MEMBER 1 - ADMIN FUNCTIONS
    // =========================================================

    fun getAllSpareParts(
        onSuccess: (List<SparePart>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        sparePartsCollection
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
        val documentReference =
            sparePartsCollection.document()

        sparePart.id = documentReference.id

        documentReference
            .set(sparePart)
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
            onFailure(
                Exception("Spare part ID not found")
            )
            return
        }

        sparePartsCollection
            .document(sparePart.id)
            .set(sparePart)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    // =========================================================
    // SHARED / MEMBER 3 FUNCTIONS
    // =========================================================

    fun getAvailableSparePartsByBranch(
        branchId: String,
        onSuccess: (List<SparePart>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        sparePartsCollection
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->

                val spareParts =
                    result.documents.mapNotNull { document ->

                        document.toObject(
                            SparePart::class.java
                        )?.apply {
                            id = document.id
                        }
                    }.filter {
                        it.quantity > 0
                    }

                onSuccess(spareParts)
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
        sparePartsCollection
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("categoryId", requiredPartId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->

                val sparePart =
                    result.documents
                        .mapNotNull { document ->

                            document.toObject(
                                SparePart::class.java
                            )?.apply {
                                id = document.id
                            }
                        }
                        .firstOrNull {
                            it.quantity > 0
                        }

                onSuccess(sparePart)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun getSparePartById(
        sparePartId: String,
        onSuccess: (SparePart?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        sparePartsCollection
            .document(sparePartId)
            .get()
            .addOnSuccessListener { document ->

                val sparePart =
                    document.toObject(
                        SparePart::class.java
                    )?.apply {
                        id = document.id
                    }

                onSuccess(sparePart)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun reduceSparePartQuantity(
        sparePartId: String,
        usedQuantity: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sparePartReference =
            sparePartsCollection.document(sparePartId)

        db.runTransaction { transaction ->

            val snapshot =
                transaction.get(sparePartReference)

            val currentQuantity =
                snapshot.getLong("quantity") ?: 0L

            if (usedQuantity <= 0) {
                throw Exception(
                    "Invalid quantity"
                )
            }

            if (currentQuantity < usedQuantity) {
                throw Exception(
                    "Not enough spare parts available"
                )
            }

            val newQuantity =
                currentQuantity - usedQuantity

            transaction.update(
                sparePartReference,
                "quantity",
                newQuantity
            )

            transaction.update(
                sparePartReference,
                "isAvailable",
                newQuantity > 0
            )
        }
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
        sparePartsCollection
            .document(sparePartId)
            .update(
                "isAvailable",
                isAvailable
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}