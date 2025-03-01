package com.spendly.backend.dto

import java.time.LocalDate

data class GoalRequest(
    val name: String,
    val targetAmount: Double,
    val targetDate: LocalDate?
)