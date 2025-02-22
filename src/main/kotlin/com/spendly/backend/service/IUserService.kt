package com.spendly.backend.service

import com.spendly.backend.entity.User

interface IUserService {
    fun findByUsername(username: String): User?
    fun saveUser(user: User): User
}
