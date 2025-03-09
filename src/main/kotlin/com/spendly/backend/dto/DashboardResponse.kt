package com.spendly.backend.dto

data class DashboardResponse(
    val welcomeMessage: String,
    val totalTransactions: Int? = null,
    val totalIncome: Double? = null,
    val totalExpense: Double? = null,
    val upcomingGoals: List<String>? = null,
    val totalUsers: Int? = null  // Only for Admin dashboard
)