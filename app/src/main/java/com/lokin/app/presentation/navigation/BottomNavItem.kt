package com.lokin.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, "Trang chủ",  Icons.Default.Home)
    object Weight    : BottomNavItem(Screen.Weight.route,    "Cân nặng",   Icons.Default.MonitorWeight)
    object Water     : BottomNavItem(Screen.Water.route,     "Nước uống",  Icons.Default.WaterDrop)
    object Exercise  : BottomNavItem(Screen.Exercise.route,  "Vận động",   Icons.Default.FitnessCenter)
    object Profile   : BottomNavItem(Screen.Profile.route,   "Cá nhân",    Icons.Default.Person)
}