package com.spendly.backend.repository

import com.spendly.backend.entity.Goal
import org.springframework.data.mongodb.repository.MongoRepository

interface GoalRepository : MongoRepository<Goal, String> {
    fun findByUserId(userId: String): List<Goal>
}