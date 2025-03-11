package com.spendly.backend.util

import com.spendly.backend.entity.Transaction
import java.io.FileWriter
import java.io.IOException

object CsvUtil {

    fun writeTransactionsToCsv(transactions: List<Transaction>, filePath: String) {
        try {
            FileWriter(filePath).use { writer ->
                writer.append("ID,User ID,Type,Amount,Currency,Date,Category,Tags,Description,Recurring,Recurrence Period\n")
                for (transaction in transactions) {
                    writer.append(transaction.id).append(',')
                    writer.append(transaction.userId).append(',')
                    writer.append(transaction.type.name).append(',')
                    writer.append(transaction.amount.toString()).append(',')
                    writer.append(transaction.currency).append(',')
                    writer.append(transaction.date.toString()).append(',')
                    writer.append(transaction.category).append(',')
                    writer.append(transaction.tags.joinToString(";")).append(',')
                    writer.append(transaction.description ?: "").append(',')
                    writer.append(transaction.recurring.toString()).append(',')
                    writer.append(transaction.recurrencePeriod ?: "").append('\n')
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
} 