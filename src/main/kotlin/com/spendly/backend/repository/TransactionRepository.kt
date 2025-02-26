package com.spendly.backend.repository

import com.spendly.backend.entity.Transaction
import org.springframework.data.mongodb.repository.MongoRepository

interface TransactionRepository : MongoRepository<Transaction, String> {
    fun findByUserId(userId: String): List<Transaction>
}