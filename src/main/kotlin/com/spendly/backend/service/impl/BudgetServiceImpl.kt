package com.spendly.backend.service.impl

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.entity.Budget
import com.spendly.backend.entity.User
import com.spendly.backend.repository.BudgetRepository
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IBudgetService
import com.spendly.backend.service.IEmailService
import com.spendly.backend.types.TransactionType
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory

@Service
class BudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val userRepository: UserRepository,
    private val emailService: IEmailService
) : IBudgetService {

    private val logger = LoggerFactory.getLogger(BudgetServiceImpl::class.java)

    @Transactional
    override fun createOrUpdateBudget(userId: String, request: BudgetRequest): Budget {
        val existingBudget = budgetRepository.findByUserIdAndCategory(userId, request.category)
        val budget = existingBudget?.copy(
            limitAmount = request.limitAmount,
            period = request.period
        ) ?: Budget(
            userId = userId,
            category = request.category,
            limitAmount = request.limitAmount,
            period = request.period,
            currentSpent = 0.0
        )
        return budgetRepository.save(budget)
    }

    override fun getBudgets(userId: String): List<Budget> {
        return budgetRepository.findByUserId(userId)
    }

    override fun getBudgetByCategory(userId: String, category: String): Budget? {
        return budgetRepository.findByUserIdAndCategory(userId, category)
    }

    @Transactional
    override fun deleteBudget(userId: String, category: String) {
        val budget = budgetRepository.findByUserIdAndCategory(userId, category)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found for category: $category")
        
        if (budget.userId != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this budget")
        }
        
        budgetRepository.delete(budget)
    }

    @Transactional
    override fun checkBudgetLimit(userId: String, category: String, transactionAmount: Double, type: TransactionType) {
        val budget = budgetRepository.findByUserIdAndCategory(userId, category)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found for category: $category")
        
        val user = userRepository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        val newSpentAmount = when (type) {
            TransactionType.INCOME -> budget.currentSpent - transactionAmount // Decrease spent amount for income
            TransactionType.EXPENSE -> budget.currentSpent + transactionAmount // Increase spent amount for expense
        }
        
        // For expenses, check if it would exceed budget limit
        if (type == TransactionType.EXPENSE && newSpentAmount > budget.limitAmount) {
            sendBudgetExceededNotification(user, budget, transactionAmount)
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Transaction would exceed budget limit of ${budget.limitAmount} for category '${budget.category}'"
            )
        }

        // For expenses, check threshold notifications
        if (type == TransactionType.EXPENSE) {
            // Check if transaction would exceed 80% of budget limit
            if (newSpentAmount > budget.limitAmount * 0.8 && budget.currentSpent <= budget.limitAmount * 0.8) {
                sendBudgetThresholdNotification(user, budget, 80.0)
            }

            // Check if transaction would exceed 90% of budget limit
            if (newSpentAmount > budget.limitAmount * 0.9 && budget.currentSpent <= budget.limitAmount * 0.9) {
                sendBudgetThresholdNotification(user, budget, 90.0)
            }
        }

        // Update current spent amount
        budget.currentSpent = newSpentAmount
        budgetRepository.save(budget)
    }

    @Async
    private fun sendBudgetExceededNotification(user: User, budget: Budget, transactionAmount: Double) {
        try {
            emailService.sendBudgetExceededNotification(user, budget, transactionAmount)
            logger.info("Budget exceeded notification sent to user ${user.email} for category ${budget.category}")
        } catch (e: Exception) {
            logger.error("Failed to send budget exceeded notification to user ${user.email} for category ${budget.category}", e)
            // Don't throw the exception as this is an async operation
        }
    }

    @Async
    private fun sendBudgetThresholdNotification(user: User, budget: Budget, thresholdPercentage: Double) {
        try {
            emailService.sendBudgetThresholdNotification(user, budget, thresholdPercentage)
            logger.info("Budget threshold notification sent to user ${user.email} for category ${budget.category} at ${thresholdPercentage}%")
        } catch (e: Exception) {
            logger.error("Failed to send budget threshold notification to user ${user.email} for category ${budget.category} at ${thresholdPercentage}%", e)
            // Don't throw the exception as this is an async operation
        }
    }
}
