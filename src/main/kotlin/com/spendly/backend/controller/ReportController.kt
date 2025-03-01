package com.spendly.backend.controller

import com.spendly.backend.dto.ReportRequest
import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.service.IReportService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: IReportService
) {
    @PostMapping
    fun generateReport(@RequestBody request: ReportRequest): ResponseEntity<ReportResponse> {
        // Retrieve user identifier from Security Context
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val report = reportService.generateReport(userId, request)
        return ResponseEntity.ok(report)
    }
}