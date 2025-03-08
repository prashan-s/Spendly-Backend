package com.spendly.backend.service

import com.spendly.backend.entity.ExchangeRate

interface IExchangeRateService {
    fun updateExchangeRates(): ExchangeRate
    fun getLatestExchangeRates(): ExchangeRate?
}