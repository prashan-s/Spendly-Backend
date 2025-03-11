package com.spendly.backend.service

import com.spendly.backend.entity.Budget
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.User

interface IEmailService {
    fun sendVerificationEmail(user: User)
    fun sendGoalProgressNotification(user: User, goal: Goal)
    fun sendBudgetExceededNotification(user: User, budget: Budget, transactionAmount: Double)
    fun sendBudgetThresholdNotification(user: User, budget: Budget, thresholdPercentage: Double)
}
