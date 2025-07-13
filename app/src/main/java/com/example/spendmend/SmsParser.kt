package com.example.spendmend

import android.util.Log
import com.example.spendmend.Transaction

object SmsParser {

    fun parseTransactionFromSms(message: String): Transaction? {
        Log.d("SmsParser", "Parsing SMS: $message")

        val amountRegex = Regex("""(?:INR|Rs|₹)\s?([\d,]+\.?\d*)""")
        val dateRegex = Regex("""on\s(\d{1,2}[-/][A-Za-z]{3,}|\d{1,2}[-/]\d{1,2}[-/]\d{2,4})""", RegexOption.IGNORE_CASE)
        val merchantRegex = Regex("""at\s([A-Za-z0-9\s&]*)""", RegexOption.IGNORE_CASE)

        val amountMatch = amountRegex.find(message)
        val dateMatch = dateRegex.find(message)
        val merchantMatch = merchantRegex.find(message)

        val amount = amountMatch?.groups?.get(1)?.value?.replace(",", "")?.toDoubleOrNull()
        val date = dateMatch?.groups?.get(1)?.value ?: ""
        val merchant = merchantMatch?.groups?.get(1)?.value ?: "Unknown Merchant"

        // 🔥 New logic: Always set a transaction type, even if no keywords found
        val transactionType = when {
            message.contains("debited", ignoreCase = true) -> "Debit"
            message.contains("credited", ignoreCase = true) -> "Credit"
            else -> "Info" // 👈 Default to "Info" if neither debited nor credited found
        }

        return if (amount != null) {
            Transaction(
                amount = amount,
                description = message,
                date = date,
                merchant = merchant.trim(),
                transactionType = transactionType
            )
        } else {
            Log.d("SmsParser", "Amount not found. Transaction is null.")
            null
        }
    }
}
