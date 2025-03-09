package com.spendly.backend.service.impl

import com.spendly.backend.dto.DashboardResponse
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IDashboardService
import com.spendly.backend.types.Role
import org.springframework.stereotype.Service

@Service
class DashboardServiceImpl(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : IDashboardService {
    override fun getDashboardData(email: String): DashboardResponse {
        val user = userRepository.findUserByEmail(email)
            ?: throw RuntimeException("User not found")
        return if (user.role == Role.ADMIN) {
            //Overall system stats
            val totalUsers = userRepository.count().toInt()
            DashboardResponse(
                welcomeMessage = "Welcome Admin ${user.username}",
                totalUsers = totalUsers
            )
        } else {
            // Regular user dashboard
            // personal stats.
            val transactions = transactionRepository.findByUserId(user.id!!)
            val totalTransactions = transactions.size
            val totalIncome = transactions.filter { it.type.name == "INCOME" }
                .sumOf { it.amount }
            val totalExpense = transactions.filter { it.type.name == "EXPENSE" }
                .sumOf { it.amount }
            DashboardResponse(
                welcomeMessage = "Welcome ${user.username}",
                totalTransactions = totalTransactions,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                upcomingGoals = listOf()
            )
        }
    }
}