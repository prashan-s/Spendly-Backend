package com.spendly.backend.controller

import com.spendly.backend.dto.ReportRequest
import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IReportService
import com.spendly.backend.types.ReportFormat
import com.spendly.backend.types.ReportStatus
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: IReportService
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @PostMapping
    fun generateReport(@RequestBody request: ReportRequest): ResponseEntity<ReportResponse> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        val report = reportService.generateReport(userId, request)
        return ResponseEntity.ok(report)
    }

    @PostMapping("/download")
    fun downloadReport(@RequestBody request: ReportRequest): ResponseEntity<ByteArray> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        
        val reportFile = reportService.generateReportFile(userId, request)
        val fileName = "spendly-report-${LocalDate.now().format(dateFormatter)}.${request.format.name.lowercase()}"
        
        val headers = HttpHeaders().apply {
            contentType = when (request.format) {
                ReportFormat.PDF -> MediaType.APPLICATION_PDF
                ReportFormat.CSV -> MediaType.parseMediaType("text/csv")
            }
            contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName)
                .build()
        }
        
        return ResponseEntity(reportFile.toByteArray(), headers, HttpStatus.OK)
    }

    @GetMapping("/{reportId}/status")
    fun getReportStatus(@PathVariable reportId: String): ResponseEntity<ReportStatus> {
        val status = reportService.getReportStatus(reportId)
        return ResponseEntity.ok(status)
    }
}