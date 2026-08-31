package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.Branch

class BranchRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getBranches(
        onSuccess: (List<Branch>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("branches")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { result ->

                val branches = result.documents.mapNotNull { document ->

                    document.toObject(Branch::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(branches)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}