package com.spendly.backend.controller

import com.spendly.backend.dto.DashboardResponse
import com.spendly.backend.service.IDashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dashboard")
class DashboardController(
    private val dashboardService: IDashboardService
) {
    @GetMapping
    fun getDashboard(): ResponseEntity<DashboardResponse> {
        // Retrieve the authenticated username from the Security Context.
        val username = SecurityContextHolder.getContext().authentication.principal as String
        val dashboard = dashboardService.getDashboardData(username)
        return ResponseEntity.ok(dashboard)
    }
}