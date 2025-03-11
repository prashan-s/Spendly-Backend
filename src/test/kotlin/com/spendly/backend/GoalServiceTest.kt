package com.spendly.backend

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.entity.Goal
import com.spendly.backend.repository.GoalRepository
import com.spendly.backend.service.impl.GoalServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class GoalServiceTest {

    private lateinit var goalService: GoalServiceImpl
    private val goalRepository: GoalRepository = mockk()

    @BeforeEach
    fun setup() {
        goalService = GoalServiceImpl(goalRepository)
    }

    @Test
    fun `createGoal should save and return goal`() {
        val userId = "user123"
        val request = GoalRequest("Save for vacation", 1000.0, null)
        val goal = Goal(userId, "Save for vacation", 1000.0, null)

        every { goalRepository.save(any()) } returns goal

        val createdGoal = goalService.createGoal(userId, request)

        assertNotNull(createdGoal)
        assertEquals("Save for vacation", createdGoal.name)
        verify { goalRepository.save(any()) }
    }

    @Test
    fun `getGoals should return list of goals for user`() {
        val userId = "user123"
        val goals = listOf(Goal(userId, "Save for vacation", 1000.0, null))

        every { goalRepository.findByUserId(userId) } returns goals

        val retrievedGoals = goalService.getGoals(userId)

        assertNotNull(retrievedGoals)
        assertEquals(1, retrievedGoals.size)
        assertEquals("Save for vacation", retrievedGoals[0].name)
    }

    @Test
    fun `updateGoal should update and return goal`() {
        val userId = "user123"
        val goalId = "goal123"
        val request = GoalRequest("Save for car", 2000.0, null)
        val existingGoal = Goal(userId, "Save for vacation", 1000.0, null)

        every { goalRepository.findById(goalId) } returns Optional.of(existingGoal)
        every { goalRepository.save(any()) } returns existingGoal.copy(name = "Save for car", targetAmount = 2000.0)

        val updatedGoal = goalService.updateGoal(userId, goalId, request)

        assertNotNull(updatedGoal)
        assertEquals("Save for car", updatedGoal.name)
        assertEquals(2000.0, updatedGoal.targetAmount)
    }

    @Test
    fun `updateGoal should throw exception if goal not found`() {
        val userId = "user123"
        val goalId = "goal123"
        val request = GoalRequest("Save for car", 2000.0, null)

        every { goalRepository.findById(goalId) } returns Optional.empty()

        assertThrows<RuntimeException> {
            goalService.updateGoal(userId, goalId, request)
        }
    }

    @Test
    fun `updateGoal should throw exception if userId does not match`() {
        val userId = "user123"
        val goalId = "goal123"
        val request = GoalRequest("Save for car", 2000.0, null)
        val existingGoal = Goal("otherUser", "Save for vacation", 1000.0, null)

        every { goalRepository.findById(goalId) } returns Optional.of(existingGoal)

        assertThrows<RuntimeException> {
            goalService.updateGoal(userId, goalId, request)
        }
    }
} 