package com.spendly.backend.service

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.Transaction

interface IGoalService {
    fun createGoal(userId: String, request: GoalRequest): Goal
    fun getGoals(userId: String): List<Goal>
    fun updateGoal(userId: String, goalId: String, request: GoalRequest): Goal
    fun updateGoalProgress(transaction: Transaction)
}