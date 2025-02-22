package com.spendly.backend.service.impl

import com.spendly.backend.entity.User
import com.spendly.backend.repository.IUserRepository
import com.spendly.backend.service.IUserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: IUserRepository) : IUserService {
    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun saveUser(user: User): User {
        return userRepository.save(user)
    }
}
