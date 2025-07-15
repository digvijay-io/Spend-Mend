package com.example.spendmend.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*

import com.example.spendmend.data.Transaction
import com.example.spendmend.ui.theme.TransactionItem

// -----------------------------
// Sealed class for routes
// -----------------------------
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Insights : Screen("insights", Icons.Default.BarChart, "Insights")
    object Notifications : Screen("notifications", Icons.Default.Notifications, "Notifications")
    object Settings : Screen("settings", Icons.Default.Settings, "Settings")
}

// -----------------------------
// Root Composable with Scaffold & NavHost
// -----------------------------
@Composable
fun MainBottomNavScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Insights.route) { PlaceholderScreen("Insights") }
            composable(Screen.Notifications.route) { PlaceholderScreen("Notifications") }
            composable(Screen.Settings.route) { PlaceholderScreen("Settings") }
        }
    }
}

// -----------------------------
// Bottom Navigation UI
// -----------------------------
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(Screen.Home, Screen.Insights, Screen.Notifications, Screen.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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



// -----------------------------
// Home Screen (SMS & DB List)
// -----------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "SMS permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // Ask for SMS permission
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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF239947)
                )
            )
        }
    ) { padding ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions yet")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}



// -----------------------------
// Placeholder Screen for other tabs
// -----------------------------
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Coming soon: $title", style = MaterialTheme.typography.titleLarge)
    }
}
