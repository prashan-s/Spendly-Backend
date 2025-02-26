package com.spendly.backend.service.impl

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.entity.Transaction
import com.spendly.backend.repository.TransactionRepository
import com.spendly.backend.service.ITransactionService
import org.springframework.stereotype.Service

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository
) : ITransactionService {
    override fun createTransaction(userId: String, request: CreateTransactionRequest): Transaction {
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
        return transactionRepository.save(transaction)
    }

    override fun getTransactions(userId: String): List<Transaction> {
        return transactionRepository.findByUserId(userId)
    }
}