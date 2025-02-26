package com.spendly.backend.service

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.entity.Transaction

interface ITransactionService {
    fun createTransaction(userId: String, request: CreateTransactionRequest): Transaction
    fun getTransactions(userId: String): List<Transaction>
}