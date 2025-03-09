package com.spendly.backend.service

import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.entity.User
import org.springframework.security.core.userdetails.UserDetailsService

interface IUserService {
    fun userDetailsService(): UserDetailsService?
    fun registerUser(request: RegisterRequest): User
    fun findUserByEmail(email: String): User?
}
