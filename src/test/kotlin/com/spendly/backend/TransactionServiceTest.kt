package com.spendly.backend

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.entity.Transaction
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.service.impl.TransactionServiceImpl
import com.spendly.backend.types.TransactionType
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class TransactionServiceTest {

    private lateinit var transactionService: TransactionServiceImpl
    private val transactionRepository: TransactionRepository = mockk()

    @BeforeEach
    fun setup() {
        transactionService = TransactionServiceImpl(transactionRepository)
    }

    @Test
    fun `createTransaction should save and return transaction`() {
        val userId = "user123"
        val request = CreateTransactionRequest(
            type = TransactionType.EXPENSE,
            amount = 100.0,
            currency = "USD",
            date = LocalDateTime.now(),
            category = "Groceries",
            tags = listOf("food", "essentials"),
            description = "Weekly grocery shopping",
            recurring = false,
            recurrencePeriod = null
        )
        val transaction = Transaction(
            userId = userId,
            type = request.type,
            amount = request.amount,
            currency = request.currency,
            date = request.date,
            category = request.category,
            tags = request.tags,
            description = request.description,
            recurring = request.recurring,
            recurrencePeriod = request.recurrencePeriod
        )

        every { transactionRepository.save(any()) } returns transaction

        val savedTransaction = transactionService.createTransaction(userId, request)

        assertNotNull(savedTransaction)
        assertEquals(userId, savedTransaction.userId)
        assertEquals(request.amount, savedTransaction.amount)
        assertEquals(request.category, savedTransaction.category)
        verify { transactionRepository.save(any()) }
    }

    @Test
    fun `getTransactions should return list of transactions for user`() {
        val userId = "user123"
        val transactions = listOf(
            Transaction(
                userId = userId,
                type = TransactionType.EXPENSE,
                amount = 50.0,
                currency = "USD",
                date = LocalDateTime.now(),
                category = "Dining",
                tags = listOf("restaurant", "food"),
                description = "Dinner at a restaurant",
                recurring = false,
                recurrencePeriod = null
            ),
            Transaction(
                userId = userId,
                type = TransactionType.INCOME,
                amount = 500.0,
                currency = "USD",
                date = LocalDateTime.now(),
                category = "Salary",
                tags = listOf("work", "monthly"),
                description = "Monthly salary",
                recurring = false,
                recurrencePeriod = null
            )
        )

        every { transactionRepository.findByUserId(userId) } returns transactions

        val result = transactionService.getTransactions(userId)

        assertEquals(2, result.size)
        assertEquals("Dining", result[0].category)
        assertEquals("Salary", result[1].category)
        verify { transactionRepository.findByUserId(userId) }
    }

    @Test
    fun `getTransactions should return empty list when no transactions exist`() {
        val userId = "user123"
        every { transactionRepository.findByUserId(userId) } returns emptyList()

        val result = transactionService.getTransactions(userId)

        assertTrue(result.isEmpty())
        verify { transactionRepository.findByUserId(userId) }
    }
}