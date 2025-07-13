package com.example.spendmend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.spendmend.AppDatabase
import com.example.spendmend.SmsParser

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in messages) {
                val messageBody = sms.messageBody

                val transaction = SmsParser.parseTransactionFromSms(messageBody)
                if (transaction == null) {
                    Log.d("SmsReceiver", "Transaction parsing failed for SMS: $messageBody")
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(context)
                            .transactionDao()
                            .insert(transaction)
                        Log.d("SmsReceiver", "Transaction inserted: $transaction")
                    }
                }

            }
        }
    }
}
