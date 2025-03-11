package com.spendly.backend.service

import com.spendly.backend.dto.ReportResponse
import java.io.ByteArrayOutputStream

interface ReportGenerator {
    fun generateReport(reportData: ReportResponse): ByteArrayOutputStream
    fun getContentType(): String
    fun getFileExtension(): String
} 