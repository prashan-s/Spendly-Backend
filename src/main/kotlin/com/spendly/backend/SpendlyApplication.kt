package com.spendly.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableScheduling
@EnableMethodSecurity
class SpendlyApplication

fun main(args: Array<String>) {
    runApplication<SpendlyApplication>(*args)
}
