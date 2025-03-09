package com.spendly.backend.service

import com.spendly.backend.dto.DashboardResponse

interface IDashboardService {
    fun getDashboardData(email: String): DashboardResponse
}