package com.spendly.backend.service.impl

import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.service.ReportGenerator
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.time.format.DateTimeFormatter

@Component
class CsvReportGenerator : ReportGenerator {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun generateReport(reportData: ReportResponse): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        val writer = OutputStreamWriter(outputStream)

        // Write Summary Section
        writer.write("SUMMARY\n")
        writer.write("Period,${reportData.period.startDate.format(dateFormatter)},${reportData.period.endDate.format(dateFormatter)}\n")
        writer.write("Total Income,${reportData.summary.totalIncome}\n")
        writer.write("Total Expense,${reportData.summary.totalExpense}\n")
        writer.write("Net Savings,${reportData.summary.netSavings}\n")
        writer.write("Total Transactions,${reportData.summary.totalTransactions}\n")
        writer.write("Average Transaction Amount,${reportData.summary.averageTransactionAmount}\n\n")

        // Write Budget Breakdown
        if (reportData.budgetBreakdown != null) {
            writer.write("BUDGET BREAKDOWN\n")
            writer.write("Category,Limit Amount,Current Spent,Remaining Amount,Percentage Used\n")
            reportData.budgetBreakdown.forEach { budget ->
                writer.write("${budget.category},${budget.limitAmount},${budget.currentSpent},${budget.remainingAmount},${budget.percentageUsed}\n")
            }
            writer.write("\n")
        }

        // Write Goal Breakdown
        if (reportData.goalBreakdown != null) {
            writer.write("GOAL BREAKDOWN\n")
            writer.write("Name,Target Amount,Current Amount,Progress Percentage,Status\n")
            reportData.goalBreakdown.forEach { goal ->
                writer.write("${goal.name},${goal.targetAmount},${goal.currentAmount},${goal.progressPercentage},${goal.status}\n")
            }
            writer.write("\n")
        }

        // Write Category Breakdown
        writer.write("CATEGORY BREAKDOWN\n")
        writer.write("Category,Total Amount,Transaction Count,Average Amount\n")
        reportData.categoryBreakdown.forEach { category ->
            writer.write("${category.category},${category.totalAmount},${category.transactionCount},${category.averageAmount}\n")
        }

        writer.flush()
        return outputStream
    }

    override fun getContentType(): String = "text/csv"

    override fun getFileExtension(): String = "csv"
} 