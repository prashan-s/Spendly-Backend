package com.spendly.backend.service

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.entity.Goal

interface IGoalService {
    fun createGoal(userId: String, request: GoalRequest): Goal
    fun getGoals(userId: String): List<Goal>
    fun updateGoal(userId: String, goalId: String, request: GoalRequest): Goal
}