package com.spendly.backend.controller

import com.spendly.backend.entity.Notification
import com.spendly.backend.entity.User
import com.spendly.backend.service.INotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: INotificationService) {

    @GetMapping
    fun getNotifications(): ResponseEntity<List<Notification>> {
                val user = SecurityContextHolder.getContext().authentication.principal as User

        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "User ID is null"
        )
        return ResponseEntity.ok(notificationService.getNotifications(userId))
    }

    @PutMapping("/{notificationId}/read")
    fun markAsRead(@PathVariable notificationId: String): ResponseEntity<Notification> {
        val updated = notificationService.markAsRead(notificationId)
        return ResponseEntity.ok(updated)
    }
}