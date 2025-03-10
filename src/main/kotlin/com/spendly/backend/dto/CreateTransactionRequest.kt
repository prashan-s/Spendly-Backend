package com.spendly.backend.dto

import com.spendly.backend.types.TransactionType
import java.time.LocalDateTime
import java.util.Date

data class CreateTransactionRequest(
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val date: Date,
    val category: String,
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val recurring: Boolean = false,
    val recurrencePeriod: String? = null
)