package com.spendly.backend.service.impl

import com.spendly.backend.entity.User
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IUserService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : IUserService {

    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findUserByEmail(username)
        }
    }

    override fun findUserByEmail(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun saveUser(user: User): User {
        return userRepository.save(user)
    }
}
