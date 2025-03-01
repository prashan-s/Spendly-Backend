package com.spendly.backend.dto

data class ReportResponse(
    val totalIncome: Double,
    val totalExpense: Double,
    val netSavings: Double,
    val breakdownByCategory: Map<String, Double>
)