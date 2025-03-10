package com.spendly.backend.dto

import com.spendly.backend.types.TransactionType
import java.time.LocalDateTime

data class CreateTransactionRequest(
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val date: LocalDateTime,
    val category: String,
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val recurring: Boolean = false,
    val recurrencePeriod: String? = null
)