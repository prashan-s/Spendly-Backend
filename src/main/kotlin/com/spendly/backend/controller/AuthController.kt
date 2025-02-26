package com.spendly.backend.controller

import com.spendly.backend.dto.LoginRequest
import com.spendly.backend.dto.LoginResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IUserService
import com.spendly.backend.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: IUserService,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

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