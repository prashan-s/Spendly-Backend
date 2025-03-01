package com.spendly.backend.service.impl

import com.spendly.backend.dto.ReportRequest
import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.service.IReportService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ReportServiceImpl(
    private val transactionRepository: TransactionRepository
) : IReportService {
    override fun generateReport(userId: String, request: ReportRequest): ReportResponse {
        // Define date range for the month
        val startDate = LocalDate.of(request.year, request.month, 1).atStartOfDay()
        val endDate = LocalDate.of(request.year, request.month,
            LocalDate.of(request.year, request.month, 1).lengthOfMonth()).atTime(23, 59, 59)

        // Fetch transactions for user in date range
        val transactions = transactionRepository.findByUserId(userId)
            .filter { it.date.isAfter(startDate.minusSeconds(1)) && it.date.isBefore(endDate.plusSeconds(1)) }

        var totalIncome = 0.0
        var totalExpense = 0.0
        val breakdown = mutableMapOf<String, Double>()

        transactions.forEach { tx ->
            when (tx.type) {
                com.spendly.backend.types.TransactionType.INCOME -> {
                    totalIncome += tx.amount
                }
                com.spendly.backend.types.TransactionType.EXPENSE -> {
                    totalExpense += tx.amount
                    // Sum expenses per category
                    breakdown[tx.category] = (breakdown[tx.category] ?: 0.0) + tx.amount
                }
            }
        }

        val netSavings = totalIncome - totalExpense
        return ReportResponse(totalIncome, totalExpense, netSavings, breakdown)
    }
}