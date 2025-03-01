package com.spendly.backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "budgets")
data class Budget(
    @Id val id: String? = null,
    val userId: String,
    val category: String,  // e.g., "Food", "Transport", or "ALL" for overall budget
    val limitAmount: Double,
    val period: String = "MONTHLY"  // default period
)
