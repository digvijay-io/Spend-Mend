package com.example.spendmend.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {

    data object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Rounded.Home
    )

    data object Insights : BottomNavItem(
        "insights",
        "Insights",
        Icons.Rounded.Analytics
    )

    data object Notifications : BottomNavItem(
        "notifications",
        "Notifications",
        Icons.Rounded.Notifications
    )

    data object Settings : BottomNavItem(
        "settings",
        "Settings",
        Icons.Rounded.Settings
    )

}

private val bottomItems = listOf(

    BottomNavItem.Home,

    BottomNavItem.Insights,

    BottomNavItem.Notifications,

    BottomNavItem.Settings

)

@Composable
fun MainBottomNavScreen() {

    val navController = rememberNavController()

    Scaffold(

        modifier = Modifier.fillMaxSize(),

        bottomBar = {

            SpendMendBottomBar(
                navController = navController,
                items = bottomItems
            )

        }

    ) { padding ->

        MainNavigationGraph(

            navController = navController,

            padding = padding

        )

    }

}

@Composable
private fun MainNavigationGraph(

    navController: androidx.navigation.NavHostController,

    padding: PaddingValues

) {

    NavHost(

        navController = navController,

        startDestination = BottomNavItem.Home.route,

        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()

    ) {

        composable(BottomNavItem.Home.route) {

            HomeScreen()

        }

        composable(BottomNavItem.Insights.route) {

            InsightsScreen()

        }

        composable(BottomNavItem.Notifications.route) {

            NotificationScreen()

        }

        composable(BottomNavItem.Settings.route) {

            SettingsScreen()

        }

    }

}

@Composable
private fun NotificationScreen() {

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {

        androidx.compose.material3.Text(
            "Notifications"
        )

    }

}

@Composable
private fun SettingsScreen() {

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {

        androidx.compose.material3.Text(
            "Settings"
        )

    }

}

@Composable
private fun SpendMendBottomBar(
    navController: androidx.navigation.NavHostController,
    items: List<BottomNavItem>
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry
            ?.destination
            ?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {

        Surface(

            tonalElevation = 6.dp,

            shadowElevation = 12.dp,

            shape = RoundedCornerShape(32.dp),

            color = Color.White

        ) {

            Row(

                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),

                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {

                items.forEach { item ->

                    BottomBarItem(

                        selected = currentRoute == item.route,

                        item = item

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

}

@Composable
private fun BottomBarItem(

    selected: Boolean,

    item: BottomNavItem,

    onClick: () -> Unit

) {

    val width by animateDpAsState(

        targetValue =
            if (selected)
                56.dp
            else
                44.dp,

        label = ""

    )

    val background by animateColorAsState(

        targetValue =
            if (selected)
                Color(0xFF239947)
            else
                Color.Transparent,

        label = ""

    )

    val iconColor by animateColorAsState(

        targetValue =
            if (selected)
                Color.White
            else
                Color.Gray,

        label = ""

    )

    Box(

        modifier = Modifier

            .widthIn(min = width)

            .height(48.dp)

            .clip(CircleShape)

            .background(background)

            .clickable {

                onClick()

            },

        contentAlignment = Alignment.Center

    ) {

        Icon(

            imageVector = item.icon,

            contentDescription = item.title,

            tint = iconColor,

            modifier = Modifier.size(24.dp)

        )

    }

}