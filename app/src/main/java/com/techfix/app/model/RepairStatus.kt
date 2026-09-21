package com.techfix.app.model

enum class RepairStatus {
    PENDING,
    CONFIRMED,
    DEVICE_RECEIVED,
    DIAGNOSING,
    REPAIRING,
    QUALITY_CHECK,
    READY_FOR_COLLECTION,
    COMPLETED
}