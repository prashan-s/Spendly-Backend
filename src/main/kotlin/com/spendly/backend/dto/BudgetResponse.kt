package com.spendly.backend.dto

data class BudgetResponse(
    val id: String,
    val userId: String,
    val category: String,
    val limitAmount: Double,
    val period: String
)
