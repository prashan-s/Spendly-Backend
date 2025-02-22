package com.spendly.backend.repository
import com.spendly.backend.entity.User
import org.springframework.data.mongodb.repository.MongoRepository

interface IUserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
}