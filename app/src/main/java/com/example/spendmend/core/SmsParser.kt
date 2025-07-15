import com.example.spendmend.data.Transaction

object SmsParser {
    fun parse(message: String): Transaction? {
        val amountRegex = Regex("""(?:INR|Rs\.?|₹)\s?([\d,]+\.?\d*)""")
        val dateRegex = Regex("""(?:on|On)\s+(\d{1,2}[-/][A-Za-z]{3,}|\d{1,2}[-/]\d{1,2}[-/]\d{2,4})""")
        val merchantRegex = Regex("""(?:at|to|for)\s+([A-Za-z0-9\s&@#.-]+)""", RegexOption.IGNORE_CASE)

        val amount = amountRegex.find(message)?.groups?.get(1)?.value?.replace(",", "")?.toDoubleOrNull()
        val date = dateRegex.find(message)?.groups?.get(1)?.value ?: ""
        val merchant = merchantRegex.find(message)?.groups?.get(1)?.value?.trim() ?: "Unknown"

        val type = when {
            message.contains("debited", true) -> "Debit"
            message.contains("credited", true) -> "Credit"
            else -> "Info"
        }

        return amount?.let {
            Transaction(
                amount = it,
                description = message,
                date = date,
                merchant = merchant,
                transactionType = type
            )
        }
    }
}
