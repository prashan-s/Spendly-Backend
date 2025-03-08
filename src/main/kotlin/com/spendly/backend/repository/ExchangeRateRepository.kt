package com.spendly.backend.repository

import com.spendly.backend.entity.ExchangeRate
import org.springframework.data.mongodb.repository.MongoRepository

interface ExchangeRateRepository : MongoRepository<ExchangeRate, String> {
    fun findTopByOrderByLastUpdatedDesc(): ExchangeRate?
}