package com.spendly.backend.service.impl

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.entity.Budget
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.Transaction
import com.spendly.backend.entity.User
import com.spendly.backend.repository.BudgetRepository
import com.spendly.backend.repository.GoalRepository
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IEmailService
import com.spendly.backend.service.IGoalService
import com.spendly.backend.service.ITransactionService
import com.spendly.backend.service.IBudgetService
import com.spendly.backend.types.GoalStatus
import com.spendly.backend.types.TransactionType
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode
import org.slf4j.LoggerFactory

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val goalService: IGoalService,
    private val goalRepository: GoalRepository,
    private val budgetRepository: BudgetRepository,
    private val userRepository: UserRepository,
    private val emailService: IEmailService,
    private val budgetService: IBudgetService
) : ITransactionService {
    
    private val logger = LoggerFactory.getLogger(TransactionServiceImpl::class.java)

    @Transactional
    override fun createTransaction(userId: String, request: CreateTransactionRequest): Transaction {
        try {
            // Validate goal if goalId is provided
            if (request.goalId != null) {
                val goal = goalRepository.findById(request.goalId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Goal with ID ${request.goalId} not found") }
                
                if (goal.userId != userId) {
                    throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to link this transaction to the specified goal")
                }
                
                if (goal.status == GoalStatus.COMPLETED) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot link transaction to a completed goal")
                }
            }

            // Validate budget and send notifications if needed
            budgetService.checkBudgetLimit(userId, request.category, request.amount, request.type)

            val transaction = Transaction(
                userId = userId,
                type = request.type,
                amount = request.amount,
                currency = request.currency,
                date = request.date,
                category = request.category,
                tags = request.tags,
                description = request.description,
                recurring = request.recurring,
                recurrencePeriod = request.recurrencePeriod,
                goalId = request.goalId
            )
            
            val savedTransaction = transactionRepository.save(transaction)
            
            // Update goal progress if goalId is provided
            if (request.goalId != null) {
                goalService.updateGoalProgress(savedTransaction)
            }
            
            return savedTransaction
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create transaction: ${e.message}"
            )
        }
    }

    override fun getTransactions(userId: String): List<Transaction> {
        return try {
            transactionRepository.findByUserId(userId)
        } catch (e: Exception) {
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to retrieve transactions: ${e.message}"
            )
        }
    }

    @Async
    fun sendBudgetExceededNotification(userId: String, category: String, budget: Budget) {
        try {
            val user = userRepository.findById(userId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID $userId not found") }
            emailService.sendBudgetExceededNotification(user, budget, budget.currentSpent)
            logger.info("Budget exceeded notification sent to user ${user.email} for category $category")
        } catch (e: Exception) {
            logger.error("Failed to send budget exceeded notification to user $userId for category $category", e)

        }
    }
}