package com.spendly.backend.service

import com.spendly.backend.dto.ReportRequest
import com.spendly.backend.dto.ReportResponse

interface IReportService {
    fun generateReport(userId: String, request: ReportRequest): ReportResponse
}