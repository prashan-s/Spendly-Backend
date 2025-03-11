package com.spendly.backend.repository

import com.spendly.backend.entity.Transaction
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransactionRepository : MongoRepository<Transaction, String> {
    fun findByUserId(userId: String): List<Transaction>
    fun findByUserIdAndDateBetween(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): List<Transaction>
}