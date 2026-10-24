package com.example.motoinventory.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {

    fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp").replace(",00", "")
    }

    fun formatRupiahShort(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> "Rp ${amount / 1_000_000_000}M"
            amount >= 1_000_000 -> "Rp ${amount / 1_000_000}jt"
            amount >= 1_000 -> "Rp ${amount / 1_000}rb"
            else -> formatRupiah(amount)
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun currentYearMonth(): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }

    fun bulanIni(): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    fun parseRupiah(text: String): Long {
        return text.replace("[^0-9]".toRegex(), "").toLongOrNull() ?: 0L
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Selamat Pagi"
            hour < 15 -> "Selamat Siang"
            hour < 18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    fun formatRupiahInput(amount: Long): String {
        if (amount == 0L) return ""
        return String.format("%,d", amount).replace(",", ".")
    }
}
