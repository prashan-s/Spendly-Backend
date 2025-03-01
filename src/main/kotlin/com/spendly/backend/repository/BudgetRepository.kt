package com.spendly.backend.repository

import com.spendly.backend.entity.Budget
import org.springframework.data.mongodb.repository.MongoRepository

interface BudgetRepository : MongoRepository<Budget, String> {
    fun findByUserId(userId: String): List<Budget>
    fun findByUserIdAndCategory(userId: String, category: String): Budget?
}
