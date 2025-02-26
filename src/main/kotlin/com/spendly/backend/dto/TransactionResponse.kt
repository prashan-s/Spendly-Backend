package com.spendly.backend.dto

import com.spendly.backend.types.TransactionType
import java.time.LocalDateTime

data class TransactionResponse(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val date: LocalDateTime,
    val category: String,
    val tags: List<String>,
    val description: String?,
    val recurring: Boolean,
    val recurrencePeriod: String?
)