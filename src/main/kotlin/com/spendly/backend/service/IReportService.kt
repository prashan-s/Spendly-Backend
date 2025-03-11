package com.spendly.backend.service

import com.spendly.backend.dto.ReportRequest
import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.types.ReportStatus
import java.io.ByteArrayOutputStream

interface IReportService {
    fun generateReport(userId: String, request: ReportRequest): ReportResponse
    fun generateReportFile(userId: String, request: ReportRequest): ByteArrayOutputStream
    fun getReportStatus(reportId: String): ReportStatus
}

enum class ReportStatus {
    PENDING,
    GENERATING,
    COMPLETED,
    FAILED
}