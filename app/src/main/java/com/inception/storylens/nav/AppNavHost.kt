package com.inception.storylens.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inception.storylens.repository.AuthRepository
import com.inception.storylens.repository.JournalRepository
import com.inception.storylens.repository.TaskRepository
import com.inception.storylens.ui.auth.*
import com.inception.storylens.ui.calendar.CalendarScreen
import com.inception.storylens.ui.home.HomeScreenRoute
import com.inception.storylens.ui.journal.*
import com.inception.storylens.viewmodel.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import com.inception.storylens.ui.intro.SplashScreen
import com.inception.storylens.ui.intro.OnboardingScreen
import com.inception.storylens.ui.profile.ProfileScreen
import com.inception.storylens.ui.profile.EditProfileScreen
import com.inception.storylens.ui.profile.ChangePasswordScreen
import com.inception.storylens.viewmodel.ProfileViewModel
import com.inception.storylens.viewmodel.ProfileViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val supabase = createSupabaseClient(
        supabaseUrl = "https://xhuwbvtbwiqxyjwzxhbd.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhodXdidnRid2lxeHlqd3p4aGJkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA0OTQ3ODIsImV4cCI6MjA2NjA3MDc4Mn0.T4p7tKXhkX4jHWS7DPX5q2WOH9iXPZ9xDDO6PKs33kE"
    ) {
        install(Auth)
        install(Storage)
        install(Postgrest)
    }

    val authRepository = AuthRepository(firebaseAuth, firestore, supabase, context)
    val journalRepository = JournalRepository(firebaseAuth, firestore, supabase, context)
    val taskRepository = TaskRepository(firebaseAuth, firestore)

    val authViewModelFactory = AuthViewModelFactory(firebaseAuth, firestore, supabase, context)
    val homeViewModelFactory = HomeViewModelFactory(authRepository, journalRepository)
    val journalViewModelFactory = JournalViewModelFactory(journalRepository)
    val calendarViewModelFactory = CalendarViewModelFactory(taskRepository)
    val profileViewModelFactory = ProfileViewModelFactory(authRepository)

    val journalViewModel: JournalViewModel = viewModel(factory = journalViewModelFactory)
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

    NavHost(navController = navController, startDestination = "splash") {

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
            HomeScreenRoute(
                homeViewModel = homeViewModel,
                navController = navController
            )
        }

        composable("journal") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                JournalScreen(viewModel = journalViewModel, navController = navController)
            }
        }

        composable("add_journal") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                AddJournalScreen(viewModel = journalViewModel, navController = navController)
            }
        }

        composable(
            route = "edit_journal/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val journalId = backStackEntry.arguments?.getString("journalId")

                LaunchedEffect(journalId) {
                    if (journalId != null) {
                        journalViewModel.getJournalById(journalId)
                    }
                }

                val journalState by journalViewModel.uiState.collectAsState()

                EditJournalScreen(
                    journalState = journalState,
                    navController = navController,
                    onUpdate = { title, note, uri ->
                        journalId?.let { id ->
                            journalViewModel.updateJournal(id, title, note, uri)
                        }
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = "view_journal/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val journalId = backStackEntry.arguments?.getString("journalId")

                LaunchedEffect(journalId) {
                    if (journalId != null) {
                        journalViewModel.getJournalById(journalId)
                    }
                }

                val journalState by journalViewModel.uiState.collectAsState()

                ViewJournalScreen(
                    journalState = journalState,
                    onNavigateBack = { navController.popBackStack() },
                    onEdit = {
                        journalId?.let { id ->
                            navController.navigate("edit_journal/$id")
                        }
                    },
                    onDelete = {
                        journalId?.let { id -> journalViewModel.deleteJournal(id) }
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("calendar") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val calendarViewModel: CalendarViewModel = viewModel(factory = calendarViewModelFactory)
                CalendarScreen(
                    navController = navController,
                    viewModel = calendarViewModel
                )
            }
        }

        composable("profile") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
                ProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("edit_profile") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
                EditProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable("change_password") {
            // Guard navigasi: Pastikan pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            } else {
                val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
                ChangePasswordScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable("splash") {
            val splashViewModel: SplashViewModel = viewModel()
            LaunchedEffect(Unit) {
                splashViewModel.navigationEvent.collect { route ->
                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            SplashScreen(navController = navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
    }
}