package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.Payment

class PaymentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val paymentsCollection = db.collection("payments")

    fun recordPayment(
        payment: Payment,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val documentReference = paymentsCollection.document()

        payment.id = documentReference.id

        documentReference
            .set(payment)
            .addOnSuccessListener {
                onSuccess(payment.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPaymentByRepairId(
        repairId: String,
        onSuccess: (Payment?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        paymentsCollection
            .whereEqualTo("repairId", repairId)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                val document = result.documents.firstOrNull()

                val payment = document
                    ?.toObject(Payment::class.java)
                    ?.apply {
                        id = document.id
                    }

                onSuccess(payment)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPaymentsByCustomer(
        customerId: String,
        onSuccess: (List<Payment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        paymentsCollection
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { result ->

                val payments = result.documents.mapNotNull { document ->

                    document.toObject(Payment::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(payments)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}