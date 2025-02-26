package com.spendly.backend.controller

import com.spendly.backend.dto.LoginRequest
import com.spendly.backend.dto.LoginResponse
import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.dto.RegisterResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IUserService
import com.spendly.backend.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: IUserService,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            val user: User = userService.registerUser(request)
            val token = jwtUtil.generateToken(user)
            val resp = RegisterResponse("User registered successfully",token)
            ResponseEntity.ok(resp)
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

        val token = jwtUtil.generateToken(user)
        return ResponseEntity.ok(LoginResponse(token))
    }
}