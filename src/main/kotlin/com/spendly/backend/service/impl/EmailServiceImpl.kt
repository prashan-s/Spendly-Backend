package com.spendly.backend.service.impl

import com.spendly.backend.entity.Budget
import com.spendly.backend.entity.Goal
import com.spendly.backend.entity.User
import com.spendly.backend.service.IEmailService
import com.spendly.backend.util.MailUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val mailUtil: MailUtil
) : IEmailService {

    private val logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    @Value("\${spring.mail.username}")
    private lateinit var fromEmail: String

    @Value("\${app.verification.base-url}")
    private lateinit var baseUrl: String

    private val emailStyle = """
        <style>
            body {
                font-family: Arial, sans-serif;
                line-height: 1.6;
                color: #333;
                max-width: 600px;
                margin: 0 auto;
                padding: 20px;
            }
            .header {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin-bottom: 20px;
            }
            .content {
                background-color: #ffffff;
                padding: 20px;
                border-radius: 5px;
                border: 1px solid #e9ecef;
            }
            .footer {
                margin-top: 20px;
                padding-top: 20px;
                border-top: 1px solid #e9ecef;
                font-size: 12px;
                color: #6c757d;
            }
            .highlight {
                color: #dc3545;
                font-weight: bold;
            }
            .success {
                color: #28a745;
            }
            .info {
                color: #17a2b8;
            }
        </style>
    """.trimIndent()

    override fun sendVerificationEmail(user: User) {
        try {
            val verificationLink = "$baseUrl/auth/verify?token=${user.verificationToken}"
            val subject = "Email Verification for Spendly"
            val content = """
                <!DOCTYPE html>
                <html>
                    <head>
                        $emailStyle
                    </head>
                    <body>
                        <div class="header">
                            <h2>Welcome to Spendly!</h2>
                        </div>
                        <div class="content">
                            <p>Dear ${user.username},</p>
                            <p>Thank you for joining Spendly. Please verify your email by clicking the button below:</p>
                            <p style="text-align: center;">
                                <a href="$verificationLink" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">
                                    Verify Email
                                </a>
                            </p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message, please do not reply.</p>
                        </div>
                    </body>
                </html>
            """.trimIndent()
            
            mailUtil.sendEmail(user.email, subject, content)
        } catch (e: Exception) {
            logger.error("Failed to send verification email to user ${user.email}", e)
            throw e
        }
    }

    override fun sendGoalProgressNotification(user: User, goal: Goal) {
        try {
            val progress = (goal.currentAmount / goal.targetAmount) * 100
            val subject = "Goal Progress Update: ${goal.name}"
            val content = """
                <!DOCTYPE html>
                <html>
                    <head>
                        $emailStyle
                    </head>
                    <body>
                        <div class="header">
                            <h2>Goal Progress Update</h2>
                        </div>
                        <div class="content">
                            <p>Dear ${user.username},</p>
                            <p>Great news! Your goal "${goal.name}" has reached <span class="success">${String.format("%.1f", progress)}%</span> of its target!</p>
                            <p><strong>Current Amount:</strong> $${goal.currentAmount}</p>
                            <p><strong>Target Amount:</strong> $${goal.targetAmount}</p>
                            <p>Keep up the great work!</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message, please do not reply.</p>
                        </div>
                    </body>
                </html>
            """.trimIndent()
            
            mailUtil.sendEmail(user.email, subject, content)
        } catch (e: Exception) {
            logger.error("Failed to send goal progress notification to user ${user.email} for goal ${goal.name}", e)
            throw e
        }
    }

    override fun sendBudgetExceededNotification(user: User, budget: Budget, transactionAmount: Double) {
        try {
            val subject = "Budget Limit Exceeded Alert"
            val content = """
                <!DOCTYPE html>
                <html>
                    <head>
                        $emailStyle
                    </head>
                    <body>
                        <div class="header">
                            <h2>Budget Limit Exceeded Alert</h2>
                        </div>
                        <div class="content">
                            <p>Dear ${user.username},</p>
                            <p>This is to notify you that your recent transaction of <span class="highlight">$${transactionAmount}</span> would exceed your budget limit of <span class="highlight">$${budget.limitAmount}</span> for the category '<span class="info">${budget.category}</span>'.</p>
                            <p><strong>Current spent:</strong> $${budget.currentSpent}</p>
                            <p><strong>Budget limit:</strong> $${budget.limitAmount}</p>
                            <p><strong>Transaction amount:</strong> $${transactionAmount}</p>
                            <p>Please review your spending and adjust your budget if necessary.</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message, please do not reply.</p>
                        </div>
                    </body>
                </html>
            """.trimIndent()

            mailUtil.sendEmail(user.email, subject, content)
        } catch (e: Exception) {
            logger.error("Failed to send budget exceeded notification to user ${user.email} for category ${budget.category}", e)
            throw e
        }
    }

    override fun sendBudgetThresholdNotification(user: User, budget: Budget, thresholdPercentage: Double) {
        try {
            val subject = "Budget Threshold Alert"
            val content = """
                <!DOCTYPE html>
                <html>
                    <head>
                        $emailStyle
                    </head>
                    <body>
                        <div class="header">
                            <h2>Budget Threshold Alert</h2>
                        </div>
                        <div class="content">
                            <p>Dear ${user.username},</p>
                            <p>This is to notify you that you have reached <span class="highlight">${thresholdPercentage}%</span> of your budget limit for the category '<span class="info">${budget.category}</span>'.</p>
                            <p><strong>Current spent:</strong> $${budget.currentSpent}</p>
                            <p><strong>Budget limit:</strong> $${budget.limitAmount}</p>
                            <p><strong>Threshold:</strong> ${thresholdPercentage}%</p>
                            <p>Please be mindful of your spending to stay within your budget.</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message, please do not reply.</p>
                        </div>
                    </body>
                </html>
            """.trimIndent()

            mailUtil.sendEmail(user.email, subject, content)
        } catch (e: Exception) {
            logger.error("Failed to send budget threshold notification to user ${user.email} for category ${budget.category}", e)
            throw e
        }
    }
}
