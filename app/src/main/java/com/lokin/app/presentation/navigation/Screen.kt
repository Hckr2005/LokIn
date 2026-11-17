package com.lokin.app.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login    : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")

    // Main (bottom nav)
    object Dashboard  : Screen("dashboard")
    object Weight     : Screen("weight")
    object Water      : Screen("water")
    object Exercise   : Screen("exercise")
    object Profile    : Screen("profile")

    // Detail screens
    object Sleep      : Screen("sleep")
    object Nutrition  : Screen("nutrition")
    object Goals      : Screen("goals")
    object Vitals     : Screen("vitals")
    object History    : Screen("history")
    object Streaks    : Screen("streaks")
    object Report     : Screen("report")
    object Main : Screen("main")
}