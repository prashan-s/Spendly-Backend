package com.spendly.backend.dto

import com.spendly.backend.types.ReportFormat
import java.time.LocalDate

data class ReportRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val format: ReportFormat = ReportFormat.PDF,
    val includeBudgets: Boolean = true,
    val includeGoals: Boolean = true,
    val categories: List<String>? = null
)