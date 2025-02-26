package com.spendly.backend.dto

import com.spendly.backend.types.Role

data class RegisterResponse(
    val message: String,
    val token: String
)