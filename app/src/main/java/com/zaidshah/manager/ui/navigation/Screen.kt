package com.zaidshah.manager.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Goals : Screen("goals")
    data object Loans : Screen("loans")
    data object Settings : Screen("settings")
}
