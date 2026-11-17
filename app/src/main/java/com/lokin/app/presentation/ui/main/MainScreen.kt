package com.lokin.app.presentation.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lokin.app.presentation.navigation.BottomNavItem
import com.lokin.app.presentation.navigation.Screen
import com.lokin.app.presentation.ui.dashboard.DashboardScreen
import com.lokin.app.presentation.ui.exercise.ExerciseScreen
import com.lokin.app.presentation.ui.goals.GoalsScreen
import com.lokin.app.presentation.ui.profile.ProfileScreen
import com.lokin.app.presentation.ui.sleep.SleepScreen
import com.lokin.app.presentation.ui.water.WaterScreen
import com.lokin.app.presentation.ui.weight.WeightScreen
import com.lokin.app.presentation.ui.history.HistoryScreen
import com.lokin.app.presentation.ui.vitals.VitalScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Weight,
        BottomNavItem.Water,
        BottomNavItem.Exercise,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDest = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDest?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            // Nếu tab đã nằm trong back stack (ví dụ Trang chủ), quay thẳng về nó
                            val popped = navController.popBackStack(item.route, inclusive = false)
                            if (!popped) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(navController) }
            composable(Screen.Weight.route)    { WeightScreen() }
            composable(Screen.Water.route)     { WaterScreen() }
            composable(Screen.Exercise.route)  { ExerciseScreen() }
            composable(Screen.Profile.route)   { ProfileScreen() }
            // Màn chi tiết (không nằm trên thanh dưới)
            composable(Screen.Sleep.route)     { SleepScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Goals.route)     { GoalsScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Vitals.route)    { VitalScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.History.route)   { HistoryScreen(onBack = { navController.popBackStack() }) }
        }
    }
}