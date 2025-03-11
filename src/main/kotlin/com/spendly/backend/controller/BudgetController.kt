package com.spendly.backend.controller

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.dto.BudgetResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IBudgetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/budgets")
class BudgetController(private val budgetService: IBudgetService) {

    @PostMapping
    fun createOrUpdateBudget(@RequestBody request: BudgetRequest): ResponseEntity<BudgetResponse> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )

        val budget = budgetService.createOrUpdateBudget(userId, request)
        val response = BudgetResponse(
            id = budget.id ?: "",
            userId = budget.userId,
            category = budget.category,
            limitAmount = budget.limitAmount,
            period = budget.period,
            currentSpent = budget.currentSpent
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getBudgets(): ResponseEntity<List<BudgetResponse>> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )

        val budgets = budgetService.getBudgets(userId)
        val responses = budgets.map { budget ->
            BudgetResponse(
                id = budget.id ?: "",
                userId = budget.userId,
                category = budget.category,
                limitAmount = budget.limitAmount,
                period = budget.period,
                currentSpent = budget.currentSpent
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/{category}")
    fun getBudgetByCategory(@PathVariable category: String): ResponseEntity<BudgetResponse> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )

        val budget = budgetService.getBudgetByCategory(userId, category)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found for category: $category")

        val response = BudgetResponse(
            id = budget.id ?: "",
            userId = budget.userId,
            category = budget.category,
            limitAmount = budget.limitAmount,
            period = budget.period,
            currentSpent = budget.currentSpent
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{category}")
    fun deleteBudget(@PathVariable category: String): ResponseEntity<Void> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )

        budgetService.deleteBudget(userId, category)
        return ResponseEntity.noContent().build()
    }
}
