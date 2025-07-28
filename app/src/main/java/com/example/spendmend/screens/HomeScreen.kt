package com.example.spendmend.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.* // Navigation

import com.example.spendmend.data.Transaction
import com.example.spendmend.ui.theme.TransactionItem

// -----------------------------
// Bottom Navigation Destinations
// -----------------------------
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Insights : Screen("insights", Icons.Default.BarChart, "Insights")
    object Notifications : Screen("notifications", Icons.Default.Notifications, "Notifications")
    object Settings : Screen("settings", Icons.Default.Settings, "Settings")
}

// -----------------------------
// Root Scaffold with NavHost + BottomNav
// -----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomNavScreen() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Insights, Screen.Notifications, Screen.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Spend Mend",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF239947)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route)
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Insights.route) { InsightsScreen() }
            composable(Screen.Notifications.route) { PlaceholderScreen("Notifications") }
            composable(Screen.Settings.route) { PlaceholderScreen("Settings") }
        }
    }
}

// -----------------------------
// Home Screen – Transaction List & SMS Permission
// -----------------------------
@Composable
fun HomeScreen(viewModel: TransactionViewModel = viewModel()) {
    val context = LocalContext.current
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "SMS permission required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
    }

    val transactions = viewModel.transactions.collectAsState().value

    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No transactions yet")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }

    // TODO DUmmy vuttion
//    Button(onClick = { viewModel.insertDummyTransactions() }) {
//        Text("Add Dummy Data")
//    }

}

// -----------------------------
// Optional: Insights Pie Chart Screen
// -----------------------------
//@Composable
//fun InsightsScreen(viewModel: TransactionViewModel = viewModel()) {
//    val categoryData = viewModel.categorySummary.collectAsState().value
//    val totalExpense = viewModel.totalExpense.collectAsState().value
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Spending by Category",
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold
//        )
//
//        if (categoryData.isEmpty()) {
//            Spacer(Modifier.height(16.dp))
//            Text("No spending data available.")
//        } else {
//            categoryData.forEach { (category, amount) ->
//                Text("$category: ₹${amount}", style = MaterialTheme.typography.bodyLarge)
//            }
//
//            Spacer(Modifier.height(16.dp))
//            Text("Total Spent: ₹$totalExpense", fontWeight = FontWeight.Bold)
//        }
//    }
//}

// -----------------------------
// Placeholder for future tabs
// -----------------------------
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Coming soon: $title", style = MaterialTheme.typography.titleLarge)
    }
}
