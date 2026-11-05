package com.example.pulsefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pulsefit.data.local.AppDatabase
import com.example.pulsefit.data.remote.RetrofitInstance
import com.example.pulsefit.data.repository.AuthRepository
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.ui.achievements.AchievementsScreen
import com.example.pulsefit.ui.auth.AuthViewModel
import com.example.pulsefit.ui.auth.LoginScreen
import com.example.pulsefit.ui.auth.RegisterScreen
import com.example.pulsefit.ui.auth.SplashScreen
import com.example.pulsefit.ui.home.HomeScreen
import com.example.pulsefit.ui.home.HomeViewModel
import com.example.pulsefit.ui.logger.ActivityLoggerScreen
import com.example.pulsefit.ui.logger.ActivityViewModel
import com.example.pulsefit.ui.settings.ProfileSettingsScreen
import com.example.pulsefit.ui.settings.SettingsViewModel
import com.example.pulsefit.ui.squads.SquadsScreen
import android.content.Context
import com.example.pulsefit.util.LocaleHelper

class MainActivity : ComponentActivity() {
    // ⬇ Ensures the language is applied to the entire activity on every launch
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)
        val repo = PulseFitRepository(db, RetrofitInstance.api)
        val authRepo = AuthRepository()

        setContent {
            MaterialTheme {
                PulseFitApp(repo, authRepo)
            }
        }
    }
}

@Composable
fun PulseFitApp(repo: PulseFitRepository, authRepo: AuthRepository) {

    val navController = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val authState by authVm.state.collectAsState()

    // ---------- Track current Firebase user ----------
    var currentUserName by remember { mutableStateOf(authRepo.displayName()) }
    var currentUserEmail by remember { mutableStateOf(authRepo.email()) }
    var currentUserId by remember { mutableStateOf(authRepo.userId()) }

    LaunchedEffect(authState.isLoggedIn) {
        currentUserName = authRepo.displayName()
        currentUserEmail = authRepo.email()
        currentUserId = authRepo.userId().ifBlank { "guest" }
    }

    // ---------- Hide bottom bar on splash / login / register ----------
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != null &&
            currentRoute !in listOf("splash", "login", "register")

    // ---------- Scaffold ----------
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple("home", Icons.Default.Home, "Home"),
                        Triple("log", Icons.Default.Edit, "Log"),
                        Triple("squads", Icons.Default.People, "Squads"),
                        Triple("progress", Icons.Default.Star, "Progress"),
                        Triple("profile", Icons.Default.Person, "Profile")
                    )
                    items.forEach { (route, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(padding)
        ) {

            // ---------- SPLASH ----------
            composable("splash") {
                SplashScreen(
                    onGetStarted = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            // ---------- AUTH ----------
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") },
                    viewModel = authVm
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = authVm
                )
            }

            // ---------- HOME ----------
            composable("home") {
                val vm: HomeViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T =
                            HomeViewModel(repo, currentUserId) as T
                    }
                )
                HomeScreen(
                    userName = currentUserName,
                    onLogWorkout = { navController.navigate("log") },
                    onOpenSquads = { navController.navigate("squads") },
                    onOpenAchievements = { navController.navigate("progress") },
                    onOpenAiCoach = { /* TODO */ },
                    viewModel = vm
                )
            }

            // ---------- LOG ----------
            composable("log") {
                val vm: ActivityViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T =
                            ActivityViewModel(repo, currentUserId) as T
                    }
                )
                ActivityLoggerScreen(vm) { navController.popBackStack() }
            }

            // ---------- SQUADS ----------
            composable("squads") { SquadsScreen() }

            // ---------- PROGRESS ----------
            composable("progress") { AchievementsScreen() }

            // ---------- PROFILE ----------
            composable("profile") {
                val vm: SettingsViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T =
                            SettingsViewModel(repo, authRepo, currentUserId) as T
                    }
                )
                ProfileSettingsScreen(
                    userEmail = currentUserEmail,
                    userName = currentUserName,
                    viewModel = vm,
                    onLogout = {
                        authVm.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}