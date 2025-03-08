package com.spendly.backend.repository

import com.spendly.backend.entity.Notification
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<Notification, String> {
    fun findByUserId(userId: String): List<Notification>
}