package com.spendly.backend.dto

import java.time.LocalDate

data class GoalResponse(
    val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: LocalDate?
)