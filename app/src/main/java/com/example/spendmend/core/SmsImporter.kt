package com.example.spendmend.core

import android.content.Context
import android.provider.Telephony
import android.util.Log
import com.example.spendmend.data.AppDatabase
import com.example.spendmend.sms.SmsParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SmsImporter {

    private const val TAG = "SMS_IMPORT"

    suspend fun importExistingSms(
        context: Context
    ) = withContext(Dispatchers.IO) {

        val dao = AppDatabase
            .getDatabase(context)
            .transactionDao()

        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.ADDRESS
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        if (cursor == null) {
            Log.e(TAG, "Unable to read SMS inbox.")
            return@withContext
        }

        val bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY)
        val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE)

        val sixMonthsAgo =
            System.currentTimeMillis() -
                    (180L * 24 * 60 * 60 * 1000)

        var imported = 0
        var skippedOld = 0
        var skippedDuplicate = 0
        var skippedNonFinancial = 0

        cursor.use {

            while (it.moveToNext()) {

                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)

                // Skip old SMS
                if (date < sixMonthsAgo) {
                    skippedOld++
                    continue
                }

                // Skip OTP & promotional SMS
                if (!isFinancialSms(body)) {
                    skippedNonFinancial++
                    continue
                }

                val transaction =
                    SmsParser.parse(body)?.copy(date = date)

                if (transaction != null) {

                    val exists =
                        dao.exists(transaction.smsHash)

                    if (!exists) {

                        dao.insert(transaction)
                        imported++

                    } else {

                        skippedDuplicate++

                    }

                }

            }

        }

        Log.d(
            TAG,
            """
Imported : $imported
Old SMS : $skippedOld
Duplicate : $skippedDuplicate
Non Financial : $skippedNonFinancial
            """.trimIndent()
        )

    }

    /**
     * Returns true only for financial / bank related SMS.
     */
    private fun isFinancialSms(message: String): Boolean {

        val text = message.lowercase()

        val keywords = listOf(

            "debited",
            "credited",
            "spent",
            "paid",
            "payment",
            "withdrawn",
            "deposit",
            "received",
            "salary",
            "refund",
            "cashback",
            "upi",
            "txn",
            "transaction",
            "a/c",
            "account",
            "available balance",
            "avl bal",
            "imps",
            "neft",
            "rtgs",
            "upi ref",
            "credited by",
            "debited by"

        )

        return keywords.any {
            text.contains(it)
        }

    }

}