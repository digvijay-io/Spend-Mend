package com.example.spendmend.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spendmend.ui.theme.BrandGreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Rounded.Home
    )

    object Analytics : BottomNavItem(
        "analytics",
        "Analytics",
        Icons.Rounded.Analytics
    )

    object History : BottomNavItem(
        "history",
        "History",
        Icons.Rounded.ReceiptLong
    )

    object Goals : BottomNavItem(
        "goals",
        "Goals",
        Icons.Rounded.Flag
    )

    object Settings : BottomNavItem(
        "settings",
        "Settings",
        Icons.Rounded.Settings
    )

}

private val bottomItems = listOf(

    BottomNavItem.Home,

    BottomNavItem.Analytics,

    BottomNavItem.History,

    BottomNavItem.Goals,

    BottomNavItem.Settings

)

@Composable
fun MainBottomNavScreen() {

    val navController = rememberNavController()

    var bottomBarVisible by remember {
        mutableStateOf(true)
    }

        Scaffold(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7FB)),

            containerColor = Color(0xFFF6F7FB),

            bottomBar = {

                AnimatedVisibility(

                    visible = bottomBarVisible,

                    enter = slideInVertically(
                        initialOffsetY = { it }
                    ) + fadeIn(),

                    exit = slideOutVertically(
                        targetOffsetY = { it }
                    ) + fadeOut()

                ) {

                    SpendMendBottomBar(

                        navController = navController,

                        items = bottomItems

                    )

                }

            }

        ) { padding ->

            MainNavigationGraph(

                navController = navController,

                padding = padding,

                onBottomBarVisibilityChanged = {

                    bottomBarVisible = it

                }

            )
        }


}

@Composable
private fun MainNavigationGraph(

    navController: NavHostController,

    padding: PaddingValues,

    onBottomBarVisibilityChanged: (Boolean) -> Unit

) {

    NavHost(

        navController = navController,

        startDestination = BottomNavItem.Home.route,

        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()

    ) {

        composable(BottomNavItem.Home.route) {

            HomeScreen(
                navController = navController,
                onBottomBarVisibilityChanged = onBottomBarVisibilityChanged
            )

        }

        composable(BottomNavItem.Analytics.route) {

            InsightsScreen(
                onBottomBarVisibilityChanged = onBottomBarVisibilityChanged
            )

        }

        composable(BottomNavItem.History.route) {

            TransactionScreen(
                onBottomBarVisibilityChanged = onBottomBarVisibilityChanged
            )

        }

        composable(BottomNavItem.Goals.route) {

            GoalsScreen(
                onGoalClick = {},
                onEditGoal = {},
                onBottomBarVisibilityChanged = onBottomBarVisibilityChanged
            )

        }

        composable(BottomNavItem.Settings.route) {

            SettingsScreen()

        }

    }

}


@Composable
private fun SettingsScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            "Settings"
        )

    }

}

@Composable
private fun SpendMendBottomBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(38.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    item = item,
                    selected = currentRoute == item.route
                ) {

                    if (currentRoute != item.route) {

                        navController.navigate(item.route) {

                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true

                        }

                    }

                }

            }

        }

    }

}


@Composable
private fun BottomBarItem(
    modifier: Modifier = Modifier,
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    val pillColor by animateColorAsState(
        targetValue = if (selected) BrandGreen else Color.Transparent,
        label = ""
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF757575),
        label = ""
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF757575),
        label = ""
    )

    Box(
        modifier = modifier
            .height(64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        if (selected) {

            Surface(
                modifier = Modifier
                    .width(72.dp)
                    .height(56.dp),
                color = BrandGreen,
                shape = RoundedCornerShape(50.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.title,
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        } else {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    color = Color(0xFF757575),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}