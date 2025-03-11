package com.spendly.backend.controller

import com.spendly.backend.dto.DashboardResponse
import com.spendly.backend.entity.User
import com.spendly.backend.service.IDashboardService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/dashboard")
class DashboardController(
    private val dashboardService: IDashboardService
) {
    @GetMapping
    fun getDashboard(): ResponseEntity<DashboardResponse> {
        // Retrieve the authenticated username from the Security Context.
        val user = SecurityContextHolder.getContext().authentication.principal as User

        val userEmail = user.email ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )

        val dashboard = dashboardService.getDashboardData(userEmail)
        return ResponseEntity.ok(dashboard)
    }
}