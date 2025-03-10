package com.spendly.backend.controller

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.dto.TransactionResponse
import com.spendly.backend.service.ITransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: ITransactionService
) {
    @PostMapping
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ResponseEntity<TransactionResponse> {
        // For simplicity, we use the authenticated username as userId.
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val transaction = transactionService.createTransaction(userId, request)

        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getTransactions(): ResponseEntity<List<TransactionResponse>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val transactions = transactionService.getTransactions(userId)
        val responses = transactions.map { tx ->
            TransactionResponse(
                id = tx.id ?: "",
                userId = tx.userId,
                type = tx.type,
                amount = tx.amount,
                currency = tx.currency,
                date = LocalDateTime.now(),
                category = tx.category,
                tags = tx.tags,
                description = tx.description,
                recurring = tx.recurring,
                recurrencePeriod = tx.recurrencePeriod
            )
        }
        return ResponseEntity.ok(responses)
    }
}