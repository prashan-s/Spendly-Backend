package com.spendly.backend.service.impl

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.spendly.backend.dto.ReportResponse
import com.spendly.backend.service.ReportGenerator
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

@Component
class PdfReportGenerator : ReportGenerator {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun generateReport(reportData: ReportResponse): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(outputStream)
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)

        // Add title
        val title = Paragraph("Spendly Financial Report")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20f)
            .setBold()
        document.add(title)
        document.add(Paragraph("Generated on: ${reportData.generatedAt.format(dateFormatter)}"))
        document.add(Paragraph("\n"))

        // Add Summary Section
        addSection(document, "Summary")
        val summaryTable = Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
        summaryTable.addCell("Period")
        summaryTable.addCell("${reportData.period.startDate.format(dateFormatter)} to ${reportData.period.endDate.format(dateFormatter)}")
        summaryTable.addCell("Total Income")
        summaryTable.addCell(reportData.summary.totalIncome.toString())
        summaryTable.addCell("Total Expense")
        summaryTable.addCell(reportData.summary.totalExpense.toString())
        summaryTable.addCell("Net Savings")
        summaryTable.addCell(reportData.summary.netSavings.toString())
        summaryTable.addCell("Total Transactions")
        summaryTable.addCell(reportData.summary.totalTransactions.toString())
        summaryTable.addCell("Average Transaction Amount")
        summaryTable.addCell(reportData.summary.averageTransactionAmount.toString())
        document.add(summaryTable)
        document.add(Paragraph("\n"))

        // Add Budget Breakdown
        if (reportData.budgetBreakdown != null) {
            addSection(document, "Budget Breakdown")
            val budgetTable = Table(UnitValue.createPercentArray(5)).useAllAvailableWidth()
            budgetTable.addHeaderCell("Category")
            budgetTable.addHeaderCell("Limit Amount")
            budgetTable.addHeaderCell("Current Spent")
            budgetTable.addHeaderCell("Remaining Amount")
            budgetTable.addHeaderCell("Percentage Used")
            
            reportData.budgetBreakdown.forEach { budget ->
                budgetTable.addCell(budget.category)
                budgetTable.addCell(budget.limitAmount.toString())
                budgetTable.addCell(budget.currentSpent.toString())
                budgetTable.addCell(budget.remainingAmount.toString())
                budgetTable.addCell("${budget.percentageUsed}%")
            }
            document.add(budgetTable)
            document.add(Paragraph("\n"))
        }

        // Add Goal Breakdown
        if (reportData.goalBreakdown != null) {
            addSection(document, "Goal Breakdown")
            val goalTable = Table(UnitValue.createPercentArray(5)).useAllAvailableWidth()
            goalTable.addHeaderCell("Name")
            goalTable.addHeaderCell("Target Amount")
            goalTable.addHeaderCell("Current Amount")
            goalTable.addHeaderCell("Progress")
            goalTable.addHeaderCell("Status")
            
            reportData.goalBreakdown.forEach { goal ->
                goalTable.addCell(goal.name)
                goalTable.addCell(goal.targetAmount.toString())
                goalTable.addCell(goal.currentAmount.toString())
                goalTable.addCell("${goal.progressPercentage}%")
                goalTable.addCell(goal.status)
            }
            document.add(goalTable)
            document.add(Paragraph("\n"))
        }

        // Add Category Breakdown
        addSection(document, "Category Breakdown")
        val categoryTable = Table(UnitValue.createPercentArray(4)).useAllAvailableWidth()
        categoryTable.addHeaderCell("Category")
        categoryTable.addHeaderCell("Total Amount")
        categoryTable.addHeaderCell("Transaction Count")
        categoryTable.addHeaderCell("Average Amount")
        
        reportData.categoryBreakdown.forEach { category ->
            categoryTable.addCell(category.category)
            categoryTable.addCell(category.totalAmount.toString())
            categoryTable.addCell(category.transactionCount.toString())
            categoryTable.addCell(category.averageAmount.toString())
        }
        document.add(categoryTable)

        document.close()
        return outputStream
    }

    private fun addSection(document: Document, title: String) {
        val sectionTitle = Paragraph(title)
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(16f)
            .setBold()
        document.add(sectionTitle)
        document.add(Paragraph("\n"))
    }

    override fun getContentType(): String = "application/pdf"

    override fun getFileExtension(): String = "pdf"
} 