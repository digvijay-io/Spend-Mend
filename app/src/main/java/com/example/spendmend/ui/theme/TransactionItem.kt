package com.example.spendmend.ui.theme


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendmend.data.Transaction

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Merchant: ${transaction.merchant}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: ₹${transaction.amount}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Date: ${transaction.date}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Type: ${transaction.transactionType}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
