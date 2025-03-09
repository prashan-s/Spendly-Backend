package com.spendly.backend.service.impl

import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.entity.User
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IEmailService
import com.spendly.backend.service.IUserService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val emailService: IEmailService
) : IUserService {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findUserByEmail(username)
        }
    }

    override fun findUserByEmail(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun registerUser(request: RegisterRequest): User {
        if (userRepository.findUserByEmail(request.email) != null) {
            throw IllegalArgumentException("Username already exists")
        }
        val hashedPassword = passwordEncoder.encode(request.password)
        val verificationToken = UUID.randomUUID().toString()

        val newUser = User(
            username = request.username,
            email = request.email,
            password = hashedPassword,
            role = request.role,
            isEmailVerified = false,                // Not enabled until verified
            verificationToken = verificationToken
        )
        val savedUser = userRepository.save(newUser)
        emailService.sendVerificationEmail(savedUser)
        return savedUser
    }

}
