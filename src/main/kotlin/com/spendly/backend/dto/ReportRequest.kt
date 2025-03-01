package com.spendly.backend.dto

data class ReportRequest(
    val month: Int, // 1 to 12
    val year: Int
)