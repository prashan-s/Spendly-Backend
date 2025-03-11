package com.spendly.backend.service.impl

import com.spendly.backend.dto.*
import com.spendly.backend.entity.Budget
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.Transaction
import com.spendly.backend.repository.BudgetRepository
import com.spendly.backend.repository.GoalRepository
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.service.IReportService
import com.spendly.backend.service.ReportGenerator
import com.spendly.backend.types.ReportFormat
import com.spendly.backend.types.ReportStatus
import com.spendly.backend.types.TransactionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Service
class ReportServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val reportGenerators: List<ReportGenerator>
) : IReportService {

    private val reportStatuses = ConcurrentHashMap<String, ReportStatus>()

    @Transactional(readOnly = true)
    override fun generateReport(userId: String, request: ReportRequest): ReportResponse {
        val transactions = transactionRepository.findByUserIdAndDateBetween(
            userId, request.startDate.atStartOfDay(), request.endDate.atTime(23, 59, 59)
        )

        val summary = calculateSummary(transactions)
        val budgetBreakdown = if (request.includeBudgets) {
            calculateBudgetBreakdown(userId, transactions)
        } else null
        val goalBreakdown = if (request.includeGoals) {
            calculateGoalBreakdown(userId)
        } else null
        val categoryBreakdown = calculateCategoryBreakdown(transactions)

        return ReportResponse(
            period = ReportPeriod(request.startDate, request.endDate),
            summary = summary,
            budgetBreakdown = budgetBreakdown,
            goalBreakdown = goalBreakdown,
            categoryBreakdown = categoryBreakdown
        )
    }

    @Transactional(readOnly = true)
    override fun generateReportFile(userId: String, request: ReportRequest): ByteArrayOutputStream {
        val reportData = generateReport(userId, request)
        val generator = reportGenerators.find { it.getFileExtension() == request.format.name.lowercase() }
            ?: throw IllegalArgumentException("Unsupported report format: ${request.format}")
        return generator.generateReport(reportData)
    }

    override fun getReportStatus(reportId: String): ReportStatus {
        return reportStatuses.getOrDefault(reportId, ReportStatus.FAILED)
    }

    private fun calculateSummary(transactions: List<Transaction>): ReportSummary {
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val totalTransactions = transactions.size
        val averageTransactionAmount = if (totalTransactions > 0) {
            (totalIncome + totalExpense) / totalTransactions
        } else 0.0

        return ReportSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netSavings = totalIncome - totalExpense,
            totalTransactions = totalTransactions,
            averageTransactionAmount = averageTransactionAmount
        )
    }

    private fun calculateBudgetBreakdown(userId: String, transactions: List<Transaction>): List<BudgetSummary> {
        val budgets = budgetRepository.findByUserId(userId)
        return budgets.map { budget ->
            val budgetTransactions = transactions.filter { it.category == budget.category }
            val currentSpent = budgetTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            val remainingAmount = budget.limitAmount - currentSpent
            val percentageUsed = (currentSpent / budget.limitAmount) * 100

            BudgetSummary(
                category = budget.category,
                limitAmount = budget.limitAmount,
                currentSpent = currentSpent,
                remainingAmount = remainingAmount,
                percentageUsed = percentageUsed
            )
        }
    }

    private fun calculateGoalBreakdown(userId: String): List<GoalSummary> {
        val goals = goalRepository.findByUserId(userId)
        return goals.map { goal ->
            val progressPercentage = (goal.currentAmount / goal.targetAmount) * 100
            GoalSummary(
                name = goal.name,
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                progressPercentage = progressPercentage,
                status = goal.status.name
            )
        }
    }

    private fun calculateCategoryBreakdown(transactions: List<Transaction>): List<CategorySummary> {
        return transactions.groupBy { it.category }
            .map { (category, categoryTransactions) ->
                val totalAmount = categoryTransactions.sumOf { it.amount }
                val transactionCount = categoryTransactions.size
                val averageAmount = if (transactionCount > 0) {
                    totalAmount / transactionCount
                } else 0.0

                CategorySummary(
                    category = category,
                    totalAmount = totalAmount,
                    transactionCount = transactionCount,
                    averageAmount = averageAmount
                )
            }
    }
}