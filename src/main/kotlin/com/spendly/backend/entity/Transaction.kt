package com.spendly.backend.entity

import com.spendly.backend.types.TransactionType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "transactions")
data class Transaction(
    @Id val id: String? = null,
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val date: LocalDateTime,
    val category: String,
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val recurring: Boolean = false,
    val recurrencePeriod: String? = null // e.g., "DAILY", "WEEKLY", "MONTHLY"
)