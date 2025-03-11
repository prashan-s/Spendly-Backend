package com.spendly.backend

import com.spendly.backend.dto.BudgetRequest
import com.spendly.backend.entity.Budget
import com.spendly.backend.repository.BudgetRepository
import com.spendly.backend.service.impl.BudgetServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BudgetServiceTest {

    private lateinit var budgetService: BudgetServiceImpl
    private val budgetRepository: BudgetRepository = mockk()

    @BeforeEach
    fun setup() {
        budgetService = BudgetServiceImpl(budgetRepository)
    }

    @Test
    fun `createOrUpdateBudget should save and return budget`() {
        val userId = "user123"
        val request = BudgetRequest("Food", 500.0, "MONTHLY")
        val budget = Budget(userId, "Food", 500.0, "MONTHLY")

        every { budgetRepository.findByUserIdAndCategory(userId, "Food") } returns null
        every { budgetRepository.save(any()) } returns budget

        val createdBudget = budgetService.createOrUpdateBudget(userId, request)

        assertNotNull(createdBudget)
        assertEquals("Food", createdBudget.category)
        verify { budgetRepository.save(any()) }
    }

    @Test
    fun `createOrUpdateBudget should update and return existing budget`() {
        val userId = "user123"
        val request = BudgetRequest("Food", 600.0, "MONTHLY")
        val existingBudget = Budget(userId, "Food", 500.0, "MONTHLY")

        every { budgetRepository.findByUserIdAndCategory(userId, "Food") } returns existingBudget
        every { budgetRepository.save(any()) } returns existingBudget.copy(limitAmount = 600.0)

        val updatedBudget = budgetService.createOrUpdateBudget(userId, request)

        assertNotNull(updatedBudget)
        assertEquals(600.0, updatedBudget.limitAmount)
    }

    @Test
    fun `getBudgets should return list of budgets for user`() {
        val userId = "user123"
        val budgets = listOf(Budget(userId, "Food", 500.0, "MONTHLY"))

        every { budgetRepository.findByUserId(userId) } returns budgets

        val retrievedBudgets = budgetService.getBudgets(userId)

        assertNotNull(retrievedBudgets)
        assertEquals(1, retrievedBudgets.size)
        assertEquals("Food", retrievedBudgets[0].category)
    }
} 