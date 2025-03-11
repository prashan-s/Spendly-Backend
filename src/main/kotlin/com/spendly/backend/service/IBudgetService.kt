package com.spendly.backend.service

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.entity.Budget
import com.spendly.backend.types.TransactionType

interface IBudgetService {
    fun createOrUpdateBudget(userId: String, request: BudgetRequest): Budget
    fun getBudgets(userId: String): List<Budget>
    fun getBudgetByCategory(userId: String, category: String): Budget?
    fun deleteBudget(userId: String, category: String)
    fun checkBudgetLimit(userId: String, category: String, transactionAmount: Double, type: TransactionType)
}
