package com.spendly.backend.service.impl

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.entity.Goal
import com.spendly.backend.repository.GoalRepository
import com.spendly.backend.service.IGoalService
import org.springframework.stereotype.Service

@Service
class GoalServiceImpl(private val goalRepository: GoalRepository) : IGoalService {
    override fun createGoal(userId: String, request: GoalRequest): Goal {
        val goal = Goal(
            userId = userId,
            name = request.name,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate
        )
        return goalRepository.save(goal)
    }

    override fun getGoals(userId: String): List<Goal> {
        return goalRepository.findByUserId(userId)
    }

    override fun updateGoal(userId: String, goalId: String, request: GoalRequest): Goal {
        val goal = goalRepository.findById(goalId)
            .orElseThrow { RuntimeException("Goal not found") }
        if (goal.userId != userId) throw RuntimeException("Access denied")
        val updatedGoal = goal.copy(
            name = request.name,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate
        )
        return goalRepository.save(updatedGoal)
    }
}