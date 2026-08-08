package com.example.spendmend.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendmend.data.model.Goal
import com.example.spendmend.data.model.GoalCategory
import com.example.spendmend.data.model.GoalPriority
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

private val SpendGreen = Color(0xFF239947)

@Composable
fun GoalCard(
    goal: Goal,
    modifier: Modifier = Modifier,
    onCardClick: (Goal) -> Unit = {},
    onAddMoney: (Goal) -> Unit,
    onEdit: (Goal) -> Unit,
    onComplete: (Goal) -> Unit,
    onDelete: (Goal) -> Unit
) {

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }


    val progress =
        if (goal.targetAmount == 0.0)
            0f
        else
            (goal.savedAmount / goal.targetAmount)
                .toFloat()
                .coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "GoalProgress"
    )

    val currency = remember {
        NumberFormat.getNumberInstance(Locale("en", "IN"))
    }

    val remaining =
        (goal.targetAmount - goal.savedAmount)
            .coerceAtLeast(0.0)

    val targetDate = remember(goal.targetDate) {
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(goal.targetDate))
    }

    val daysLeft =
        ((goal.targetDate - System.currentTimeMillis())
                / (1000 * 60 * 60 * 24))
            .toInt()

    val monthsLeft =
        ceil(
            (goal.targetDate - System.currentTimeMillis())
                .toDouble() /
                    (1000 * 60 * 60 * 24 * 30)
        )
            .toInt()
            .coerceAtLeast(1)

    val monthlySaving =
        remaining / monthsLeft



    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(20.dp)
        ) {

            // ============================
            // HEADER
            // ============================

            HeaderSection(
                goal = goal,
                menuExpanded = menuExpanded,
                onExpand = {
                    expanded = !expanded
                    onCardClick(goal)
                },
                onMenuOpen = {
                    menuExpanded = true
                },
                onMenuDismiss = {
                    menuExpanded = false
                },
                onEdit = {
                    menuExpanded = false
                    onEdit(goal)
                },
                onDelete = {
                    menuExpanded = false
                    onDelete(goal)
                },
                onComplete = {
                    menuExpanded = false
                    onComplete(goal)
                }
            )

            Spacer(Modifier.height(22.dp))

            // ============================
            // PROGRESS
            // ============================

            ProgressSection(
                progress = animatedProgress,
                saved = currency.format(goal.savedAmount),
                target = currency.format(goal.targetAmount),
                expanded = expanded,
                onExpand = {
                    expanded = !expanded
                }
            )

            AnimatedVisibility(
                visible = expanded
            ) {

                Column {

                    Spacer(Modifier.height(20.dp))

                    // Stats section comes in Part 4

                    StatsSection(
                        saved = "₹${currency.format(goal.savedAmount)}",
                        target = "₹${currency.format(goal.targetAmount)}",
                        remaining = "₹${currency.format(remaining)}",
                        monthly = "₹${currency.format(monthlySaving)}"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    DateSection(
                        targetDate = targetDate,
                        daysLeft = daysLeft
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    MotivationSection(
                        progress = progress,
                        completed = goal.isCompleted
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ActionSection(
                        completed = goal.isCompleted,
                        onAddMoney = {
                            onAddMoney(goal)
                        }
                    )

                }

            }

        }

    }
}

@Composable
private fun HeaderSection(
    goal: Goal,
    menuExpanded: Boolean,
    onExpand: () -> Unit,
    onMenuOpen: () -> Unit,
    onMenuDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {

    val priorityColor = when (goal.priority) {
        GoalPriority.HIGH -> MaterialTheme.colorScheme.error
        GoalPriority.MEDIUM -> Color(0xFFF59E0B)
        GoalPriority.LOW -> SpendGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = SpendGreen.copy(alpha = 0.12f)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = categoryIcon(goal.category),
                    contentDescription = null,
                    tint = SpendGreen,
                    modifier = Modifier.size(28.dp)
                )

            }

        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = goal.category.displayName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = SpendGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            if (goal.notes.isNotBlank()) {

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = goal.notes,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

        }

        Surface(
            shape = RoundedCornerShape(50),
            color = priorityColor.copy(alpha = .12f)
        ) {

            Text(
                text = goal.priority.name.lowercase()
                    .replaceFirstChar { it.titlecase() },
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                ),
                color = priorityColor,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

        }

        Spacer(modifier = Modifier.width(8.dp))

        Box {

            IconButton(
                onClick = onMenuOpen
            ) {

                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null
                )

            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = onMenuDismiss,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Edit Goal")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null
                        )
                    },
                    onClick = onEdit
                )

                if (!goal.isCompleted) {

                    DropdownMenuItem(
                        text = {
                            Text("Mark as Completed")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null
                            )
                        },
                        onClick = onComplete
                    )

                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Text(
                            "Delete Goal",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = onDelete
                )

            }

        }

    }

}

@Composable
private fun ProgressSection(
    progress: Float,
    saved: String,
    target: String,
    expanded: Boolean,
    onExpand: () -> Unit
) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = saved,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SpendGreen
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "/",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = target,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(100.dp)),
            color = SpendGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onExpand()
                },
            color = Color.Transparent
        ) {

            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (expanded) "Hide Details" else "View Details",
                    style = MaterialTheme.typography.labelLarge,
                    color = SpendGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = if (expanded)
                        Icons.Outlined.KeyboardArrowUp
                    else
                        Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SpendGreen
                )

            }

        }

    }

}

@Composable
private fun StatsSection(
    saved: String,
    target: String,
    remaining: String,
    monthly: String
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Savings,
                title = "Saved",
                value = saved,
                tint = SpendGreen
            )

            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Flag,
                title = "Target",
                value = target,
                tint = MaterialTheme.colorScheme.primary
            )

        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.HourglassBottom,
                title = "Remaining",
                value = remaining,
                tint = Color(0xFFF59E0B)
            )

            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                title = "Monthly",
                value = monthly,
                tint = Color(0xFF2563EB)
            )

        }

    }

}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    tint: Color
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = tint.copy(alpha = .12f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )

                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

        }

    }

}

@Composable
private fun DateSection(
    targetDate: String,
    daysLeft: Int
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = SpendGreen.copy(alpha = .12f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = SpendGreen
                    )

                }

            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Target Date",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = targetDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

            }

            Surface(
                shape = RoundedCornerShape(50.dp),
                color =
                    if (daysLeft >= 0)
                        SpendGreen.copy(alpha = .12f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = .12f)
            ) {

                Text(
                    text =
                        if (daysLeft >= 0)
                            "$daysLeft days"
                        else
                            "Overdue",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    color =
                        if (daysLeft >= 0)
                            SpendGreen
                        else
                            MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )

            }

        }

    }

}

@Composable
private fun MotivationSection(
    progress: Float,
    completed: Boolean
) {

    val message = when {

        completed ->
            "Congratulations! You achieved this goal."

        progress >= 0.80f ->
            "You're almost there. Keep going!"

        progress >= 0.40f ->
            "Excellent progress. Stay consistent."

        else ->
            "Every small contribution gets you closer."

    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = SpendGreen
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

        }

    }

}

@Composable
private fun ActionSection(
    completed: Boolean,
    onAddMoney: () -> Unit
) {

    if (completed) {

        Surface(
            shape = RoundedCornerShape(50.dp),
            color = SpendGreen.copy(alpha = .12f)
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = SpendGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Goal Completed",
                    color = SpendGreen,
                    fontWeight = FontWeight.Bold
                )

            }

        }

    } else {

        Button(
            onClick = onAddMoney,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpendGreen
            )
        ) {

            Icon(
                imageVector = Icons.Outlined.Savings,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Add Money",
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}

private fun categoryIcon(
    category: GoalCategory
): ImageVector {

    return when (category) {

        GoalCategory.LAPTOP ->
            Icons.Outlined.LaptopMac

        GoalCategory.PHONE ->
            Icons.Outlined.PhoneAndroid

        GoalCategory.TRAVEL ->
            Icons.Outlined.FlightTakeoff

        GoalCategory.EMERGENCY_FUND ->
            Icons.Outlined.LocalHospital

        GoalCategory.VEHICLE ->
            Icons.Outlined.DirectionsCar

        GoalCategory.HOME ->
            Icons.Outlined.Home

        GoalCategory.EDUCATION ->
            Icons.Outlined.School

        GoalCategory.INVESTMENT ->
            Icons.AutoMirrored.Outlined.TrendingUp

        GoalCategory.WEDDING ->
            Icons.Outlined.Favorite

        GoalCategory.BUSINESS ->
            Icons.Outlined.Work

        GoalCategory.RETIREMENT ->
            Icons.Outlined.Savings

        GoalCategory.CUSTOM ->
            Icons.Outlined.Star
    }

}

@Preview(showBackground = true)
@Composable
private fun GoalCardPreview() {

    MaterialTheme {

        GoalCard(
            goal = Goal(
                id = 1,
                title = "Buy MacBook Pro",
                category = GoalCategory.LAPTOP,
                targetAmount = 180000.0,
                savedAmount = 82000.0,
                targetDate = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 180),
                priority = GoalPriority.HIGH,
                notes = "Needed for Android Development",
                isCompleted = false
            ),
            onCardClick = {},
            onAddMoney = {},
            onEdit = {},
            onComplete = {},
            onDelete = {}
        )

    }

}