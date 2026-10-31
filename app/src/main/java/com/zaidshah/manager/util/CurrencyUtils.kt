package com.zaidshah.manager.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val inrLocale = Locale("en", "IN")

    fun formatPaise(paise: Long, showSign: Boolean = false): String {
        val rupees = paise.toDouble() / 100.0
        val formatter = NumberFormat.getCurrencyInstance(inrLocale).apply {
            maximumFractionDigits = if (paise % 100L == 0L) 0 else 2
            minimumFractionDigits = 0
        }
        val formatted = formatter.format(rupees).replace("INR", "₹").trim()
        return when {
            showSign && paise > 0 -> "+$formatted"
            showSign && paise < 0 -> formatted
            else -> formatted
        }
    }

    fun parseRupeesToPaise(input: String): Long? {
        val clean = input.replace(",", "").replace("₹", "").trim()
        if (clean.isEmpty()) return null
        val parts = clean.split(".")
        if (parts.size > 2) return null
        if (parts.size == 2 && parts[1].length > 2) return null
        return try {
            val rupees = parts[0].toLong()
            if (rupees < 0) return null
            val paise = if (parts.size == 2) {
                parts[1].padEnd(2, '0').take(2).toLong()
            } else {
                0L
            }
            (rupees * 100L) + paise
        } catch (_: Exception) {
            null
        }
    }
}
