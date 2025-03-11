package com.spendly.backend.service.impl

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.Transaction
import com.spendly.backend.entity.User
import com.spendly.backend.repository.GoalRepository
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IEmailService
import com.spendly.backend.service.IGoalService
import com.spendly.backend.types.TransactionType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class GoalServiceImpl(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val emailService: IEmailService
) : IGoalService {

    override fun createGoal(userId: String, request: GoalRequest): Goal {
        val goal = Goal(
            userId = userId,
            name = request.name,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate,
            category = request.category,
            notificationThreshold = request.notificationThreshold
        )
        return goalRepository.save(goal)
    }

    override fun getGoals(userId: String): List<Goal> {
        return goalRepository.findByUserId(userId)
    }

    override fun updateGoal(userId: String, goalId: String, request: GoalRequest): Goal {
        val goal = goalRepository.findById(goalId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Goal with ID $goalId not found") }
        
        if (goal.userId != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this goal")
        }
        
        val updatedGoal = goal.copy(
            name = request.name,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate,
            category = request.category,
            notificationThreshold = request.notificationThreshold
        )
        return goalRepository.save(updatedGoal)
    }

    @Transactional
    override fun updateGoalProgress(transaction: Transaction) {
        // Only update goals that have a matching category or no category set
        val goals = goalRepository.findByUserIdAndCategoryOrCategoryIsNull(transaction.userId, transaction.category)
        
        goals.forEach { goal ->
            val previousProgress = goal.currentAmount / goal.targetAmount
            val newAmount = when (transaction.type) {
                TransactionType.INCOME -> goal.currentAmount + transaction.amount
                TransactionType.EXPENSE -> {
                    if (goal.currentAmount - transaction.amount < 0) {
                        throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Transaction amount ${transaction.amount} exceeds current goal amount ${goal.currentAmount} for goal '${goal.name}'"
                        )
                    }
                    goal.currentAmount - transaction.amount
                }
            }
            
            val updatedGoal = goal.copy(currentAmount = newAmount)
            goalRepository.save(updatedGoal)
            
            // Check if we've crossed the notification threshold
            val newProgress = newAmount / goal.targetAmount
            if (previousProgress < goal.notificationThreshold && newProgress >= goal.notificationThreshold) {
                val user = userRepository.findById(transaction.userId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID ${transaction.userId} not found") }
                emailService.sendGoalProgressNotification(user, updatedGoal)
            }
        }
    }
}