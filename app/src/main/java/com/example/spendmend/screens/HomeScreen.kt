package com.example.spendmend.screens

import android.Manifest
import android.graphics.fonts.Font
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.data.Transaction
import com.example.spendmend.ui.theme.TransactionItem
import org.intellij.lang.annotations.JdkConstants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(context, "SMS permission required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
    }

    val viewModel: TransactionViewModel = viewModel()
    val transactions = viewModel.transactions.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Spend Mend",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF239947)
                )
            )
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No transactions yet", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}
