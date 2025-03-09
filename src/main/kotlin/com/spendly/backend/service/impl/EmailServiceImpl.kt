package com.spendly.backend.service.impl

import com.spendly.backend.entity.User
import com.spendly.backend.service.IEmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(private val mailSender: JavaMailSender) : IEmailService {

    @Value("\${spring.mail.username}")
    private lateinit var fromEmail: String

    @Value("\${app.verification.base-url}")
    private lateinit var baseUrl: String

    override fun sendVerificationEmail(user: User) {
        val verificationLink = "$baseUrl/auth/verify?token=${user.verificationToken}"
        val message = SimpleMailMessage().apply {
            setFrom(fromEmail)
            setTo(user.email)
            setSubject("Email Verification for Spendly")
            setText("Dear ${user.username},\n\nPlease verify your email by clicking the link below:\n$verificationLink\n\nThank you!")
        }
        mailSender.send(message)
    }
}
