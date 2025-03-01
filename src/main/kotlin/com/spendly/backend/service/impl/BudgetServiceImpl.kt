package com.spendly.backend.service.impl

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.entity.Budget
import com.spendly.backend.repository.BudgetRepository
import com.spendly.backend.service.IBudgetService
import org.springframework.stereotype.Service

@Service
class BudgetServiceImpl(private val budgetRepository: BudgetRepository) : IBudgetService {

    override fun createOrUpdateBudget(userId: String, request: BudgetRequest): Budget {
        val existingBudget = budgetRepository.findByUserIdAndCategory(userId, request.category)
        val budget = existingBudget?.copy(limitAmount = request.limitAmount, period = request.period)
            ?: Budget(userId = userId, category = request.category, limitAmount = request.limitAmount, period = request.period)
        return budgetRepository.save(budget)
    }

    override fun getBudgets(userId: String): List<Budget> {
        return budgetRepository.findByUserId(userId)
    }
}
