package com.zaidshah.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zaidshah.manager.data.preferences.AppPreferences
import com.zaidshah.manager.ui.components.BottomCapsuleNavBar
import com.zaidshah.manager.ui.goals.GoalsScreen
import com.zaidshah.manager.ui.goals.GoalsViewModel
import com.zaidshah.manager.ui.home.HomeScreen
import com.zaidshah.manager.ui.home.HomeViewModel
import com.zaidshah.manager.ui.loans.LoansScreen
import com.zaidshah.manager.ui.loans.LoansViewModel
import com.zaidshah.manager.ui.navigation.Screen
import com.zaidshah.manager.ui.settings.SettingsScreen
import com.zaidshah.manager.ui.settings.SettingsViewModel
import com.zaidshah.manager.ui.theme.ManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(this)

        setContent {
            val preset by prefs.themePreset.collectAsState(initial = "FOREST")
            ManagerTheme(preset = preset) {
                ManagerMainApp()
            }
        }
    }
}

@Composable
fun ManagerMainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                val homeVm: HomeViewModel = viewModel()
                HomeScreen(
                    viewModel = homeVm,
                    onNavigateToLoans = { navController.navigate(Screen.Loans.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Goals.route) {
                val goalsVm: GoalsViewModel = viewModel()
                GoalsScreen(viewModel = goalsVm)
            }
            composable(Screen.Loans.route) {
                val loansVm: LoansViewModel = viewModel()
                LoansScreen(viewModel = loansVm)
            }
            composable(Screen.Settings.route) {
                val settingsVm: SettingsViewModel = viewModel()
                SettingsScreen(viewModel = settingsVm)
            }
        }

        BottomCapsuleNavBar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                if (currentRoute != route) {
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
