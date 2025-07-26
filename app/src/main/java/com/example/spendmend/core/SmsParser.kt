import com.example.spendmend.data.Transaction

object SmsParser {
    fun parse(message: String): Transaction? {
        val amountRegex = Regex("""(?:INR|Rs\.?|₹)\s?([\d,]+\.?\d*)""")
        val dateRegex = Regex("""(?:on|On)\s+(\d{1,2}[-/][A-Za-z]{3,}|\d{1,2}[-/]\d{1,2}[-/]\d{2,4})""")
        val merchantRegex = Regex("""(?:at|to|for)\s+([A-Za-z0-9\s&@#.-]+)""", RegexOption.IGNORE_CASE)

        val amount = amountRegex.find(message)?.groups?.get(1)?.value?.replace(",", "")?.toDoubleOrNull()
        val date = dateRegex.find(message)?.groups?.get(1)?.value ?: ""
        val merchant = merchantRegex.find(message)?.groups?.get(1)?.value?.trim() ?: "Unknown"

        // Detect transaction type
        val type = when {
            message.contains("debited", true) -> "Expense" // ✅ Changed from Debit
            message.contains("credited", true) || message.contains("received", true) -> "Income"
            else -> "Info"
        }

        // Detect category
        val category = when {
            merchant.contains("zomato", true) || merchant.contains("swiggy", true) -> "Food"
            merchant.contains("ixigo", true) || merchant.contains("rapido", true) -> "Travel"
            merchant.contains("jio", true) || merchant.contains("airtel", true) -> "Bills"
            merchant.contains("amazon", true) || merchant.contains("flipkart", true) -> "Shopping"
            message.contains("salary", true) || message.contains("credited", true) -> "Income"
            else -> "Other"
        }

        return amount?.let {
            Transaction(
                amount = it,
                description = message,
                date = date,
                merchant = merchant,
                transactionType = type,
                category = category // ✅ FIXED
            )
        }
    }
}
