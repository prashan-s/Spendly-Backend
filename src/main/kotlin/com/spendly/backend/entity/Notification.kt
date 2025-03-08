package com.spendly.backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "notifications")
data class Notification(
    @Id val id: String? = null,
    val userId: String,
    val message: String,
    val type: String, // e.g. "BUDGET_ALERT", "RECURRENCE_REMINDER"
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val read: Boolean = false
)