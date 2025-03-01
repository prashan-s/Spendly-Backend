package com.spendly.backend.dto

data class BudgetRequest(
    val category: String,
    val limitAmount: Double,
    val period: String = "MONTHLY"
)
