package com.example.spendmend.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.spendmend.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                SmsParser.parse(sms.messageBody)?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.Companion.getDatabase(context).transactionDao().insert(it)
                        Log.d("SMS", "Saved: $it")
                    }
                }
            }
        }
    }
}