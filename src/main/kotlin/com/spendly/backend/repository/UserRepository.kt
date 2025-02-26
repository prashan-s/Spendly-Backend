package com.spendly.backend.repository
import com.spendly.backend.entity.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findUserByEmail(email: String): User?
}