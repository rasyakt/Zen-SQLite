package com.example.zensqlite.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return rupiahFormat.format(amount)
    }

    fun formatRupiahCompact(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> String.format("Rp%.1fM", amount / 1_000_000_000)
            amount >= 1_000_000 -> String.format("Rp%.1fJt", amount / 1_000_000)
            amount >= 1_000 -> String.format("Rp%.0fRb", amount / 1_000)
            else -> formatRupiah(amount)
        }
    }

    fun parseRupiah(text: String): Double {
        return try {
            text.replace("Rp", "")
                .replace(".", "")
                .replace(",", "")
                .replace(" ", "")
                .trim()
                .toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
