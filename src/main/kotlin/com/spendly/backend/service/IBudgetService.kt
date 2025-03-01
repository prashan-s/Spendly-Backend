package com.spendly.backend.service

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.entity.Budget

interface IBudgetService {
    fun createOrUpdateBudget(userId: String, request: BudgetRequest): Budget
    fun getBudgets(userId: String): List<Budget>
}
