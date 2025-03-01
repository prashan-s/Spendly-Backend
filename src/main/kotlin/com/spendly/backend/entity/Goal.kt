package com.spendly.backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "goals")
data class Goal(
    @Id val id: String? = null,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: LocalDate? = null
)