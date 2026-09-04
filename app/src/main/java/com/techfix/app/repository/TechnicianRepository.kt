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
            .get()
            .addOnSuccessListener { result ->

                val technicians = result.documents
                    .mapNotNull { document ->

                        document.toObject(Technician::class.java)?.apply {
                            id = document.id
                        }
                    }
                    .filter { technician ->
                        technician.isAvailable && technician.isActive
                    }

                android.util.Log.d(
                    "TECHFIX_TECH_QUERY",
                    "Branch $branchId -> ${technicians.size} available technicians"
                )

                onSuccess(technicians)
            }
            .addOnFailureListener { exception ->

                android.util.Log.e(
                    "TECHFIX_TECH_QUERY",
                    "Technician query error: ${exception.message}"
                )

                onFailure(exception)
            }
    }

    fun getAllTechnicians(
        onSuccess: (List<Technician>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("technicians")
            .get()
            .addOnSuccessListener { result ->

                val technicians = result.documents.mapNotNull { document ->

                    val technician =
                        document.toObject(Technician::class.java)?.apply {
                            id = document.id
                        }

                    if (technician != null) {
                        android.util.Log.d(
                            "TECHNICIAN_DEBUG",
                            "ID=${technician.id}, " +
                                    "Name=${technician.name}, " +
                                    "Speciality=${technician.speciality}, " +
                                    "Branch=${technician.branchId}"
                        )
                    }

                    technician
                }

                onSuccess(technicians)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addTechnician(
        technician: Technician,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val technicianData = hashMapOf<String, Any>(
            "name" to technician.name,
            "branchId" to technician.branchId,
            "phone" to technician.phone,
            "speciality" to technician.speciality,
            "isAvailable" to technician.isAvailable,
            "isActive" to technician.isActive
        )

        db.collection("technicians")
            .add(technicianData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateTechnician(
        technician: Technician,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (technician.id.isEmpty()) {
            onFailure(Exception("Technician ID is empty"))
            return
        }

        val technicianData = hashMapOf<String, Any>(
            "name" to technician.name,
            "branchId" to technician.branchId,
            "phone" to technician.phone,
            "speciality" to technician.speciality,
            "isAvailable" to technician.isAvailable,
            "isActive" to technician.isActive
        )

        db.collection("technicians")
            .document(technician.id)
            .update(technicianData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateTechnicianStatus(
        technicianId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("technicians")
            .document(technicianId)
            .update("isActive", isActive)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateTechnicianAvailability(
        technicianId: String,
        isAvailable: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("technicians")
            .document(technicianId)
            .update("isAvailable", isAvailable)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}