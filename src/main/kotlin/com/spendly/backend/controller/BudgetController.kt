package com.spendly.backend.controller

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.dto.BudgetResponse
import com.spendly.backend.service.IBudgetService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/budgets")
class BudgetController(private val budgetService: IBudgetService) {

    @PostMapping
    fun createOrUpdateBudget(@RequestBody request: BudgetRequest): ResponseEntity<BudgetResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val budget = budgetService.createOrUpdateBudget(userId, request)
        val response = BudgetResponse(
            id = budget.id ?: "",
            userId = budget.userId,
            category = budget.category,
            limitAmount = budget.limitAmount,
            period = budget.period
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getBudgets(): ResponseEntity<List<BudgetResponse>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val budgets = budgetService.getBudgets(userId)
        val responses = budgets.map { budget ->
            BudgetResponse(
                id = budget.id ?: "",
                userId = budget.userId,
                category = budget.category,
                limitAmount = budget.limitAmount,
                period = budget.period
            )
        }
        return ResponseEntity.ok(responses)
    }
}
