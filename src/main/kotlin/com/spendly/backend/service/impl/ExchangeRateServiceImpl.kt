package com.spendly.backend.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.spendly.backend.entity.ExchangeRate
import com.spendly.backend.repository.ExchangeRateRepository
import com.spendly.backend.service.IExchangeRateService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class ExchangeRateServiceImpl(
    private val exchangeRateRepository: ExchangeRateRepository
) : IExchangeRateService {

    @Value("\${exchange-rate.api-key}")
    private lateinit var apiKey: String

    @Value("\${exchange-rate.url}")
    private lateinit var apiUrl: String

    @Value("\${exchange-rate.symbols}")
    private lateinit var symbols: String

    private val restTemplate = RestTemplate()
    private val mapper = jacksonObjectMapper()

    override fun updateExchangeRates(): ExchangeRate {
        // Call external API
        val url = "$apiUrl?access_key=$apiKey&symbols=$symbols&format=1"
        val response: String = restTemplate.getForObject(url, String::class.java) ?: "{}"
        val jsonNode: JsonNode = mapper.readTree(response)

        val base = jsonNode.get("base").asText("EUR")
        val ratesNode = jsonNode.get("rates")
        val rates: Map<String, Double> = ratesNode.fields().asSequence()
            .associate { it.key to it.value.asDouble() }

        val exchangeRate = ExchangeRate(base = base, rates = rates, lastUpdated = LocalDateTime.now())
        return exchangeRateRepository.save(exchangeRate)
    }

    override fun getLatestExchangeRates(): ExchangeRate? {
        return exchangeRateRepository.findTopByOrderByLastUpdatedDesc()
    }

    // Update rates daily (cron expression from application.yml)
    @Scheduled(cron = "\${exchange-rate.update-cron}")
    fun scheduledUpdate() {
        updateExchangeRates()
    }
}