package com.spendly.backend.dto

import com.spendly.backend.types.Role

data class RegisterRequest(
    val username: String,
    val email:String,
    val password: String,
    val role: Role = Role.USER
)
