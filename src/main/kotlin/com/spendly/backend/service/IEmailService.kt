package com.spendly.backend.service

import com.spendly.backend.entity.User

interface IEmailService {
    fun sendVerificationEmail(user: User)
}
