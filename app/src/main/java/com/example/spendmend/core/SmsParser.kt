package com.example.spendmend.sms

import com.example.spendmend.core.MerchantRules
import com.example.spendmend.data.Transaction
import com.example.spendmend.data.model.Bank
import com.example.spendmend.data.model.Category
import com.example.spendmend.data.model.PaymentMethod
import com.example.spendmend.data.model.TransactionType

object SmsParser {

    fun parse(message: String): Transaction? {

        val cleanMessage = message.replace("\n", " ")

        // -----------------------------
        // Amount
        // -----------------------------

        val amountRegex = Regex(
            """(?:Rs\.?|INR|₹)\s*([\d,]+(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        val amount = amountRegex
            .find(cleanMessage)
            ?.groupValues
            ?.getOrNull(1)
            ?.replace(",", "")
            ?.toDoubleOrNull()

        if (amount == null) return null

        // -----------------------------
        // Transaction Type
        // -----------------------------

        val transactionType = when {

            Regex(
                """(a/c|account).*debited|is debited|has been debited""",
                RegexOption.IGNORE_CASE
            ).containsMatchIn(cleanMessage) ->

                TransactionType.EXPENSE

            Regex(
                """salary.*credited|(a/c|account).*credited|is credited|has been credited""",
                RegexOption.IGNORE_CASE
            ).containsMatchIn(cleanMessage) ->

                TransactionType.INCOME

            cleanMessage.contains("sent", true) ->

                TransactionType.EXPENSE

            cleanMessage.contains("paid", true) ->

                TransactionType.EXPENSE

            cleanMessage.contains("received", true) ->

                TransactionType.INCOME

            cleanMessage.contains("refund", true) ->

                TransactionType.INCOME

            cleanMessage.contains("cashback", true) ->

                TransactionType.INCOME

            else ->

                TransactionType.EXPENSE
        }

        // -----------------------------
        // Merchant
        // -----------------------------

        var merchant = "Unknown"

        val upiRegex = Regex(
            """([A-Za-z0-9._-]+)@""",
            RegexOption.IGNORE_CASE
        )

        upiRegex.find(cleanMessage)?.let {

            merchant = it.groupValues[1]

            merchant = merchant.substringBefore(".")
        }

        if (merchant == "Unknown") {

            val merchantRegex = Regex(
                """(?:to|at|for)\s+([A-Za-z0-9 &._-]+)""",
                RegexOption.IGNORE_CASE
            )

            merchantRegex.find(cleanMessage)?.let {

                merchant = it.groupValues[1]
                    .trim()
                    .substringBefore(",")

            }

        }

        merchant = merchant.replaceFirstChar {
            it.uppercase()
        }

        // -----------------------------
        // Payment Method
        // -----------------------------

        val paymentMethod = when {

            cleanMessage.contains("upi", true) ->
                PaymentMethod.UPI

            cleanMessage.contains("credit card", true) ->
                PaymentMethod.CREDIT_CARD

            cleanMessage.contains("debit card", true) ->
                PaymentMethod.DEBIT_CARD

            cleanMessage.contains("imps", true) ->
                PaymentMethod.IMPS

            cleanMessage.contains("neft", true) ->
                PaymentMethod.NEFT

            cleanMessage.contains("rtgs", true) ->
                PaymentMethod.RTGS

            cleanMessage.contains("atm", true) ->
                PaymentMethod.ATM

            else ->
                PaymentMethod.OTHER
        }

        // -----------------------------
        // Bank
        // -----------------------------

        val bank = when {

            cleanMessage.contains("HDFC", true) -> Bank.HDFC

            cleanMessage.contains("SBI", true) -> Bank.SBI

            cleanMessage.contains("ICICI", true) -> Bank.ICICI

            cleanMessage.contains("AXIS", true) -> Bank.AXIS

            cleanMessage.contains("KOTAK", true) -> Bank.KOTAK

            cleanMessage.contains("PNB", true) -> Bank.PNB

            cleanMessage.contains("CANARA", true) -> Bank.CANARA

            cleanMessage.contains("FEDERAL", true) -> Bank.FEDERAL

            cleanMessage.contains("IDFC", true) -> Bank.IDFC

            cleanMessage.contains("YES", true) -> Bank.YES_BANK

            cleanMessage.contains("INDUSIND", true) -> Bank.INDUSIND

            cleanMessage.contains("PAYTM", true) -> Bank.PAYTM

            cleanMessage.contains("PHONEPE", true) -> Bank.PHONEPE

            cleanMessage.contains("GOOGLE", true) -> Bank.GOOGLE_PAY

            else -> Bank.OTHER
        }

        // -----------------------------
        // Category
        // -----------------------------

        val merchantLower = merchant.lowercase()

        val category = MerchantRules.categoryMap.entries
            .firstOrNull { (_, merchants) ->
                merchants.any {
                    merchant.lowercase().contains(it)
                }
            }
            ?.key
            ?: if (cleanMessage.contains("salary", true))
                Category.SALARY
            else
                Category.OTHER

        return Transaction(

            amount = amount,

            merchant = merchant,

            description = cleanMessage,

            category = category,

            transactionType = transactionType,

            paymentMethod = paymentMethod,

            bankName = bank,

            accountNumber = "",

            date = System.currentTimeMillis(),

            isAutoDetected = true,

            smsBody = cleanMessage,

            smsHash = cleanMessage.hashCode().toString(),

            confidence = 0.96f


        )

    }
}