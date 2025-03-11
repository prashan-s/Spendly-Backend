package com.spendly.backend.advice

import jakarta.servlet.http.HttpServletRequest
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.net.ConnectException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException::class)
    fun handleDatabaseException(ex: DataAccessException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("<h1>SERVICE_UNAVAILABLE</h1><br>Database connection issue. Please try again later.")
    }

    @ExceptionHandler(ConnectException::class)
    fun handleDatabaseException(ex: ConnectException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("<h1>SERVICE_UNAVAILABLE</h1><br>Database connection issue. Please try again later.")
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleBundleRefillException(exception: ResponseStatusException): ResponseEntity<String> {
        return ResponseEntity
            .status(exception.statusCode)
            .body(exception.message)
    }


    @ExceptionHandler(Exception::class)
    fun handleGenericException(request: HttpServletRequest, ex: Exception): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("<h1>INTERNAL SERVER ERROR</h1><br>An unexpected error occurred$ex")
    }


}
