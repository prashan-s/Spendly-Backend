package com.spendly.backend.service

import com.spendly.backend.entity.Notification

interface INotificationService {
    fun createNotification(notification: Notification): Notification
    fun getNotifications(userId: String): List<Notification>
    fun markAsRead(notificationId: String): Notification
}