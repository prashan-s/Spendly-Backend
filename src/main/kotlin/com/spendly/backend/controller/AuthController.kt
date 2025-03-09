package com.spendly.backend.controller

import com.spendly.backend.dto.LoginRequest
import com.spendly.backend.dto.LoginResponse
import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.dto.RegisterResponse
import com.spendly.backend.entity.User
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IUserService
import com.spendly.backend.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: IUserService,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    fun register(@RequestBody request: com.spendly.backend.dto.RegisterRequest): ResponseEntity<Any> {
        return try {
            val user: User = userService.registerUser(request)
            ResponseEntity.ok("User registered successfully. Please check your email to verify your account.")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val user: User = userService.findUserByEmail(request.email)
            ?: return ResponseEntity.badRequest().body("Invalid username or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.badRequest().body("Invalid username or password")
        }

        if (!user.isEmailVerified) {
            return ResponseEntity.badRequest().body("Please verify your email before logging in")
        }

        val token = jwtUtil.generateToken(user)
        return ResponseEntity.ok(LoginResponse(token))
    }

    @GetMapping("/verify")
    fun verifyEmail(@RequestParam token: String): ResponseEntity<String> {
        val user = userRepository.findByVerificationToken(token)
            ?: return ResponseEntity.badRequest().body("Invalid verification token")

        val verifiedUser = user.copy(isEmailVerified = true, verificationToken = null)
        userRepository.save(verifiedUser)
        return ResponseEntity.ok("Email verified successfully. You can now log in.")
    }

}