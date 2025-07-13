package com.example.spendmend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlin.Unit as Unit1
import com.example.spendmend.screens.LoginScreen
import com.example.spendmend.screens.OtpVerificationScreen
import com.example.spendmend.screens.PhoneAuthScreen
import com.example.spendmend.screens.SignupScreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendmend.ui.theme.TransactionItem
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.material3.* // if using Material3
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import kotlin.Unit



// TO Do
/* 1. Add Sign Out Option
   2. Keep User Login After he Sign in or login or closes the app
   
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendMendApp()
        }
    }
}

@Composable
fun SpendMendApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen { navController.navigate("walkthrough1") } }
        composable("walkthrough1") { WalkthroughScreen1(navController) }
        composable("walkthrough2") { WalkthroughScreen2(navController) }
        composable("walkthrough3") { WalkthroughScreen3(navController) }
        composable("Login"){LoginScreen(navController)}
        composable("signup") { SignupScreen(navController) }
        composable("otp"){ PhoneAuthScreen(navController) }
        composable("verifyotp"){ OtpVerificationScreen(navController) }
        composable("home") { HomeScreen() }
        composable("success") { Onboarding(navController) }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit1) {
    LaunchedEffect(true) {
        delay(3000)
        onTimeout()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Spend Mend", fontSize = 32.sp, color = Color.White)
    }
}

@Composable
fun WalkthroughScreen1(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_1,
        title = "Welcome to Spend Mend",
        description = "Track, Save and Grow your Money like Magic",
        onNext = { navController.navigate("walkthrough2") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}

@Composable
fun WalkthroughScreen2(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_2,
        title = "Set Smart Budgets",
        description = "Spend wisely and save more.",
        onNext = { navController.navigate("walkthrough3") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}

@Composable
fun WalkthroughScreen3(navController: NavHostController) {
    CommonWalkthrough(
        imageRes = R.drawable.walkthrough_3,
        title = "",
        description = "Master your money, one smart move at a time. Track, budget, and save with   confidence.",
        onNext = { navController.navigate("login") },
        onSkip = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
    )
}


@Composable
fun CommonWalkthrough(
    imageRes: Int,
    title: String,
    description: String,
    onNext: () -> Unit1,
    onSkip: () -> Unit1
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // White background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Centered content
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Bottom buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF239947)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Next")
                }
                Text(
                    text = "Skip",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .clickable { onSkip() }
                )
            }
        }
    }
}

@Composable
fun Onboarding(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Add your image from drawable
            Image(
                painter = painterResource(id = R.drawable.success), // Rename accordingly
                contentDescription = "Success Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Success !!!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Take charge of your finances — track, save, and grow.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B050))
                ) {
                    Text("Let’s Start", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}




@Composable
fun HomeScreen() {
    val viewModel: TransactionViewModel = viewModel()
    val transactions = viewModel.transactions.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("➕ Add Transaction")
        }

        if (showDialog) {
            AddTransactionDialog(
                onDismiss = { showDialog = false },
                onAddTransaction = { transaction: Transaction ->
                    viewModel.addTransaction(transaction)
                    showDialog = false
                }
            )
        }
    }
}

// ✨ Move it OUTSIDE
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Transaction") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = transactionType,
                    onValueChange = { transactionType = it },
                    label = { Text("Type (Debit/Credit)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val transaction = Transaction(
                        id = 0,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        merchant = merchant,
                        date = date,
                        description = description,
                        transactionType = transactionType
                    )
                    onAddTransaction(transaction)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
