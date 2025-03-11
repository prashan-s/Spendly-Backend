package com.spendly.backend.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory

@Component
class MailUtil(private val mailSender: JavaMailSender) {

    private val logger = LoggerFactory.getLogger(MailUtil::class.java)

    @Value("\${spring.mail.username}")
    private lateinit var fromEmail: String

    @Async("asyncPool")
    fun sendEmail(to: String, subject: String, content: String) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")
            
            helper.setFrom(fromEmail)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(content, true)
            
            mailSender.send(message)
            logger.info("HTML email sent successfully to $to with subject: $subject")
        } catch (e: Exception) {
            logger.error("Failed to send HTML email to $to with subject: $subject", e)
            throw e
        }
    }
} 