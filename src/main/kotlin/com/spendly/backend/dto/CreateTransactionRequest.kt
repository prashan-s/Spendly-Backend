package com.spendly.backend.dto

import com.spendly.backend.types.TransactionType
import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class CreateTransactionRequest(
    @NotNull
    val type: TransactionType,

    @Min(0)
    val amount: Double,

    @NotBlank
    @Size(max = 3)
    val currency: String,

    @NotNull
    val date: LocalDateTime,

    @NotBlank
    val category: String,

    val tags: List<String> = emptyList(),

    @Size(max = 255)
    val description: String? = null,

    val recurring: Boolean = false,

    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|YEARLY")
    val recurrencePeriod: String? = null,

    val goalId: String? = null
)