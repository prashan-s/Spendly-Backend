package com.spendly.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpendlyApplication

fun main(args: Array<String>) {
    runApplication<SpendlyApplication>(*args)
}
