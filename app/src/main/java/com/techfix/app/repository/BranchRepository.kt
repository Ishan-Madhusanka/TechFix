package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.Branch

class BranchRepository {

    private val db = FirebaseFirestore.getInstance()

    // Load only active branches
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

    // Load all branches for Admin screen
    fun getAllBranches(
        onSuccess: (List<Branch>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("branches")
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

    // Update branch information
    fun updateBranch(
        branch: Branch,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (branch.id.isEmpty()) {
            onFailure(
                Exception("Branch ID is empty")
            )
            return
        }

        val branchData = hashMapOf<String, Any>(
            "name" to branch.name,
            "city" to branch.city,
            "latitude" to branch.latitude,
            "longitude" to branch.longitude,
            "isActive" to branch.isActive
        )

        db.collection("branches")
            .document(branch.id)
            .update(branchData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Change branch Active / Inactive status
    fun updateBranchStatus(
        branchId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("branches")
            .document(branchId)
            .update(
                "isActive",
                isActive
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}