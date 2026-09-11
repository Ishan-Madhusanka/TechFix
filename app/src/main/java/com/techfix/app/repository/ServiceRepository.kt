package com.techfix.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.model.Service

class ServiceRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAllServices(
        onSuccess: (List<Service>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .get()
            .addOnSuccessListener { result ->

                val services = result.documents.mapNotNull { document ->
                    document.toObject(Service::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(services)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getActiveServices(
        onSuccess: (List<Service>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { result ->

                val services = result.documents.mapNotNull { document ->
                    document.toObject(Service::class.java)?.apply {
                        id = document.id
                    }
                }

                onSuccess(services)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getServiceById(
        serviceId: String,
        onSuccess: (Service?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .document(serviceId)
            .get()
            .addOnSuccessListener { document ->


                val service = document.toObject(Service::class.java)?.apply {
                    id = document.id
                }

                onSuccess(service)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addService(
        service: Service,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .add(service)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateService(
        service: Service,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (service.id.isEmpty()) {
            onFailure(Exception("Service ID is empty"))
            return
        }

        db.collection("services")
            .document(service.id)
            .set(service)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateServiceStatus(
        serviceId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .document(serviceId)
            .update("isActive", isActive)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteService(
        serviceId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("services")
            .document(serviceId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}