package com.spendly.backend.controller

import com.spendly.backend.dto.CreateTransactionRequest
import com.spendly.backend.dto.TransactionResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.ITransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.web.server.ResponseStatusException

private val logger = LoggerFactory.getLogger(TransactionController::class.java)

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: ITransactionService
) {
    @PostMapping
    fun createTransaction(@RequestBody request: CreateTransactionRequest): ResponseEntity<Any> {
        return try {
            val user = SecurityContextHolder.getContext().authentication.principal as User
            val userId = user.id ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "User ID is null"
            )
            
            // Sanitize input
            val sanitizedRequest = sanitizeCreateTransactionRequest(request)
            val transaction = transactionService.createTransaction(userId, sanitizedRequest)
            
            ResponseEntity.ok(TransactionResponse(
                id = transaction.id ?: "",
                userId = transaction.userId,
                type = transaction.type,
                amount = transaction.amount,
                currency = transaction.currency,
                date = LocalDateTime.now(),
                category = transaction.category,
                tags = transaction.tags,
                description = transaction.description,
                recurring = transaction.recurring,
                recurrencePeriod = transaction.recurrencePeriod
            ))
        } catch (ex: ResponseStatusException) {
            logger.error("Error creating transaction: ${ex.message}")
            ResponseEntity
                .status(ex.statusCode)
                .body(mapOf(
                    "error" to ex.message,
                    "status" to ex.statusCode.value(),
                    "timestamp" to LocalDateTime.now()
                ))
        } catch (ex: Exception) {
            logger.error("Unexpected error creating transaction: ", ex)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "error" to "An unexpected error occurred while creating the transaction",
                    "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "timestamp" to LocalDateTime.now()
                ))
        }
    }

    @GetMapping
    fun getTransactions(): ResponseEntity<Any> {
        return try {
            val user = SecurityContextHolder.getContext().authentication.principal as User
            val userId = user.id ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "User ID is null"
            )
            
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
            ResponseEntity.ok(responses)
        } catch (ex: ResponseStatusException) {
            logger.error("Error retrieving transactions: ${ex.message}")
            ResponseEntity
                .status(ex.statusCode)
                .body(mapOf(
                    "error" to ex.message,
                    "status" to ex.statusCode.value(),
                    "timestamp" to LocalDateTime.now()
                ))
        } catch (ex: Exception) {
            logger.error("Unexpected error retrieving transactions: ", ex)
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "error" to "An unexpected error occurred while retrieving transactions",
                    "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "timestamp" to LocalDateTime.now()
                ))
        }
    }

    private fun sanitizeCreateTransactionRequest(request: CreateTransactionRequest): CreateTransactionRequest {
        return request
    }
}

@ControllerAdvice
class TransactionControllerAdvice {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val status = if (ex is ResponseStatusException) ex.statusCode else HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity
            .status(status)
            .body(mapOf(
                "error" to (ex.message ?: "An unexpected error occurred"),
                "status" to status.value(),
                "timestamp" to LocalDateTime.now()
            ))
    }
}