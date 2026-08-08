package com.example.spendmend.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val BrandGreen = Color(0xFF239947)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBottomSheet(

    currentBudget: Double,

    spent: Double,

    onDismiss: () -> Unit,

    onSave: (Double) -> Unit

) {

    val sheetState = rememberModalBottomSheetState()

    val focusManager = LocalFocusManager.current

    val focusRequester = remember {
        FocusRequester()
    }

    var budgetText by remember {

        mutableStateOf(
            currentBudget.toInt().toString()
        )

    }

    val budgetAmount =
        budgetText.toDoubleOrNull() ?: 0.0

    val remaining =
        (budgetAmount - spent).coerceAtLeast(0.0)

    val isValid =
        budgetAmount >= 100

//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }

    ModalBottomSheet(

        onDismissRequest = onDismiss,

        sheetState = sheetState,

        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp
        )

    ) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .verticalScroll(
                    rememberScrollState()
                )

                .padding(24.dp)

                .animateContentSize()

        ) {

//            Box(
//
//                modifier = Modifier.fillMaxWidth(),
//
//                contentAlignment = Alignment.Center
//
//            ) {
//
//                Surface(
//
//                    modifier = Modifier
//                        .width(52.dp)
//                        .height(6.dp),
//
//                    shape = CircleShape,
//
//                    color = Color.LightGray
//
//                ) {}
//
//            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Card(

                    shape = CircleShape,

                    colors = CardDefaults.cardColors(

                        containerColor = BrandGreen.copy(.10f)

                    )

                ) {

                    Icon(

                        Icons.Rounded.AccountBalanceWallet,

                        null,

                        tint = BrandGreen,

                        modifier = Modifier.padding(14.dp)

                    )

                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {

                    Text(

                        "Monthly Budget",

                        style = MaterialTheme.typography.headlineSmall,

                        fontWeight = FontWeight.Bold

                    )

                    Text(

                        "Set a spending goal for this month.",

                        color = Color.Gray

                    )

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(

                    containerColor = Color(0xFFF7F8FA)

                )

            ) {

                Column(

                    modifier = Modifier.padding(20.dp)

                ) {

                    Text(

                        "Budget Overview",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {

                        BudgetInfoItem(

                            modifier = Modifier.weight(1f),

                            title = "Current",

                            value = "₹${currentBudget.toInt()}"

                        )

                        BudgetInfoItem(

                            modifier = Modifier.weight(1f),

                            title = "Spent",

                            value = "₹${spent.toInt()}"

                        )

                        BudgetInfoItem(

                            modifier = Modifier.weight(1f),

                            title = "Left",

                            value = "₹${remaining.toInt()}"

                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(

                "Budget Amount",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(

                modifier = Modifier

                    .fillMaxWidth()

                    .focusRequester(
                        focusRequester
                    ),

                value = budgetText,

                onValueChange = {

                    if (it.all(Char::isDigit)) {

                        budgetText = it

                    }

                },

                singleLine = true,

                prefix = {

                    Text("₹")

                },

                leadingIcon = {

                    Icon(

                        Icons.Rounded.Edit,

                        null,

                        tint = BrandGreen

                    )

                },

                label = {

                    Text("Monthly Budget")

                },

                keyboardOptions = KeyboardOptions(

                    keyboardType = KeyboardType.Number,

                    imeAction = ImeAction.Done

                ),

                keyboardActions = KeyboardActions(

                    onDone = {

                        focusManager.clearFocus()

                    }

                ),

                isError =

                    budgetText.isNotBlank() && !isValid

            )

            AnimatedVisibility(

                visible =

                    budgetText.isNotBlank() && !isValid

            ) {

                Text(

                    text = "Budget should be at least ₹100",

                    color = MaterialTheme.colorScheme.error,

                    modifier = Modifier.padding(top = 8.dp)

                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Budget",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val quickBudgets = listOf(
                10000,
                20000,
                30000,
                50000,
                75000,
                100000
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                quickBudgets.forEach { amount ->

                    Card(
                        onClick = {
                            budgetText = amount.toString()
                        },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            1.dp,
                            BrandGreen.copy(.25f)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Text(
                            text = "₹${"%,d".format(amount)}",
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),
                            color = BrandGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEAF7EF)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "💡 Smart Suggestion",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val suggestion =
                        if (spent > currentBudget)
                            "You exceeded your budget last month. Consider increasing it."
                        else if (spent > currentBudget * 0.8)
                            "You're close to your budget. ₹35,000 may be more comfortable."
                        else
                            "Your current budget looks healthy."

                    Text(
                        suggestion,
                        color = Color.Gray
                    )

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            val daysLeft = remember {

                val calendar = java.util.Calendar.getInstance()

                val today =
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)

                val max =
                    calendar.getActualMaximum(
                        java.util.Calendar.DAY_OF_MONTH
                    )

                (max - today).coerceAtLeast(1)

            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7F8FA)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "Daily Spending Target",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "₹${"%,.0f".format(remaining / daysLeft)} per day",
                        style = MaterialTheme.typography.headlineSmall,
                        color = BrandGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Based on your remaining budget.",
                        color = Color.Gray
                    )

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = onDismiss
                ) {

                    Text("Cancel")

                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen
                    ),
                    onClick = {

                        focusManager.clearFocus()

                        onSave(budgetAmount)

                    }
                ) {

                    Text("Save Budget")

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

        }

    }

}

@Composable
private fun BudgetInfoItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = title,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

    }

}