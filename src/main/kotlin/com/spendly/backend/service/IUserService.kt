package com.spendly.backend.service

import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.entity.User
import org.springframework.security.core.userdetails.UserDetailsService

interface IUserService {
    fun userDetailsService(): UserDetailsService?
    fun findUserByEmail(email: String): User?
    fun saveUser(user: User): User

    fun registerUser(request: RegisterRequest): User
}
