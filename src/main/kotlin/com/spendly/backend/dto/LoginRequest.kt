package com.spendly.backend.dto

data class LoginRequest(
    var email: String,
    val password: String
)