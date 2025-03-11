package com.spendly.backend.dto

import java.time.LocalDate

data class ReportResponse(
    val period: ReportPeriod,
    val summary: ReportSummary,
    val budgetBreakdown: List<BudgetSummary>?,
    val goalBreakdown: List<GoalSummary>?,
    val categoryBreakdown: List<CategorySummary>,
    val generatedAt: LocalDate = LocalDate.now()
)

data class ReportPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class ReportSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val netSavings: Double,
    val totalTransactions: Int,
    val averageTransactionAmount: Double
)

data class BudgetSummary(
    val category: String,
    val limitAmount: Double,
    val currentSpent: Double,
    val remainingAmount: Double,
    val percentageUsed: Double
)

data class GoalSummary(
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val progressPercentage: Double,
    val status: String
)

data class CategorySummary(
    val category: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val averageAmount: Double
)