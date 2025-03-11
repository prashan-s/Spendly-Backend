package com.spendly.backend.dto

import java.time.LocalDate
import jakarta.validation.constraints.*

data class GoalRequest(
    @NotBlank(message = "Goal name cannot be blank")
    @Size(min = 3, max = 100, message = "Goal name must be between 3 and 100 characters")
    val name: String,

    @Min(value = 0, message = "Target amount must be greater than or equal to 0")
    val targetAmount: Double,

    val targetDate: LocalDate?,

    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    val category: String? = null,

    @Min(value = 0, message = "Notification threshold must be between 0 and 1")
    @Max(value = 1, message = "Notification threshold must be between 0 and 1")
    val notificationThreshold: Double = 0.8
)