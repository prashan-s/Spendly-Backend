package com.spendly.backend.dto

import jakarta.validation.constraints.*

data class BudgetRequest(
    @NotBlank(message = "Category cannot be blank")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    val category: String,

    @Min(value = 0, message = "Limit amount must be greater than or equal to 0")
    val limitAmount: Double,

    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|YEARLY", message = "Period must be one of: DAILY, WEEKLY, MONTHLY, YEARLY")
    val period: String = "MONTHLY"
)
