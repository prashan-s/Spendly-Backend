package com.spendly.backend.service.impl

import com.spendly.backend.entity.Notification
import com.spendly.backend.repository.NotificationRepository
import com.spendly.backend.service.INotificationService
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(private val notificationRepository: NotificationRepository) : INotificationService {
    override fun createNotification(notification: Notification): Notification {
        return notificationRepository.save(notification)
    }

    override fun getNotifications(userId: String): List<Notification> {
        return notificationRepository.findByUserId(userId)
    }

    override fun markAsRead(notificationId: String): Notification {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { RuntimeException("Notification not found") }
        return notificationRepository.save(notification.copy(read = true))
    }
}