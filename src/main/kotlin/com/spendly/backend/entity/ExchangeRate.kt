package com.spendly.backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "exchangeRates")
data class ExchangeRate(
    @Id val id: String? = null,
    val base: String,
    val rates: Map<String, Double>,
    val lastUpdated: LocalDateTime
)