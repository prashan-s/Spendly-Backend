package com.spendly.backend.controller

import com.spendly.backend.dto.GoalRequest
import com.spendly.backend.dto.GoalResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IGoalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/goals")
class GoalController(private val goalService: IGoalService) {

    @PostMapping
    fun createGoal(@RequestBody request: GoalRequest): ResponseEntity<GoalResponse> {
                val user = SecurityContextHolder.getContext().authentication.principal as User

        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        val goal = goalService.createGoal(userId, request)
        val response = GoalResponse(
            id = goal.id ?: "",
            userId = goal.userId,
            name = goal.name,
            targetAmount = goal.targetAmount,
            currentAmount = goal.currentAmount,
            targetDate = goal.targetDate
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getGoals(): ResponseEntity<List<GoalResponse>> {
                val user = SecurityContextHolder.getContext().authentication.principal as User

        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        val goals = goalService.getGoals(userId)
        val responses = goals.map { goal ->
            GoalResponse(
                id = goal.id ?: "",
                userId = goal.userId,
                name = goal.name,
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                targetDate = goal.targetDate
            )
        }
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/{goalId}")
    fun updateGoal(@PathVariable goalId: String, @RequestBody request: GoalRequest): ResponseEntity<GoalResponse> {
                val user = SecurityContextHolder.getContext().authentication.principal as User

        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        val goal = goalService.updateGoal(userId, goalId, request)
        val response = GoalResponse(
            id = goal.id ?: "",
            userId = goal.userId,
            name = goal.name,
            targetAmount = goal.targetAmount,
            currentAmount = goal.currentAmount,
            targetDate = goal.targetDate
        )
        return ResponseEntity.ok(response)
    }
}