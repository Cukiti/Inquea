package com.inquea.inquea.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inquea.inquea.ui.screens.auth.LoginScreen
import com.inquea.inquea.ui.screens.auth.RegisterScreen
import com.inquea.inquea.ui.screens.client.ClientMainScreen
import com.inquea.inquea.ui.screens.client.BookingScreen
import com.inquea.inquea.ui.screens.client.PaymentScreen
import com.inquea.inquea.ui.screens.client.ResolutionCenterScreen
import com.inquea.inquea.ui.screens.chat.ChatDetailScreen
import com.inquea.inquea.ui.screens.business.BusinessMainScreen
import com.inquea.inquea.ui.screens.business.BusinessProfileSetupScreen
import com.inquea.inquea.ui.screens.business.UploadReelScreen
import com.inquea.inquea.ui.screens.business.CreateFlashOfferScreen
import com.inquea.inquea.ui.screens.onboarding.OnboardingScreen
import com.inquea.inquea.ui.screens.splash.SplashScreen
import com.inquea.inquea.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register/{isBusiness}") {
        fun createRoute(isBusiness: Boolean) = "register/$isBusiness"
    }
    object ClientMain : Screen("client_main")
    object Booking : Screen("booking/{businessId}") {
        fun createRoute(businessId: String) = "booking/$businessId"
    }
    object Payment : Screen("payment")
    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
    object ResolutionCenter : Screen("resolution_center")
    object BusinessMain : Screen("business_main")
    object BusinessProfileSetup : Screen("business_profile_setup")
    object UploadReel : Screen("upload_reel")
    object CreateFlashOffer : Screen("create_flash_offer")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToClient = {
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToBusiness = {
                    navController.navigate(Screen.BusinessMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.createRoute(false))
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.createRoute(false))
                },
                onLoginSuccess = { role ->
                    if (role == "business") {
                        navController.navigate(Screen.BusinessMain.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.ClientMain.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("isBusiness") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isBusiness = backStackEntry.arguments?.getBoolean("isBusiness") ?: false
            RegisterScreen(
                isInitialBusiness = isBusiness,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { role ->
                    if (role == "business") {
                        navController.navigate(Screen.BusinessProfileSetup.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.ClientMain.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        

        composable(Screen.BusinessProfileSetup.route) {
            BusinessProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.BusinessMain.route) {
                        popUpTo(Screen.BusinessProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.BusinessMain.route) {
            BusinessMainScreen(
                onNavigateToUpload = {
                    navController.navigate(Screen.UploadReel.route)
                },
                onNavigateToFlashOffer = {
                    navController.navigate(Screen.CreateFlashOffer.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToResolution = {
                    navController.navigate(Screen.ResolutionCenter.route)
                }
            )
        }

        composable(Screen.UploadReel.route) {
            UploadReelScreen(
                onUploadSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateFlashOffer.route) {
            CreateFlashOfferScreen(
                onOfferCreated = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Client Specific Screens
        composable(Screen.ClientMain.route) {
            ClientMainScreen(
                onAuthRequired = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToBooking = { businessId ->
                    navController.navigate(Screen.Booking.createRoute(businessId))
                },
                onNavigateToChat = { chatId ->
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                },
                onNavigateToResolution = {
                    navController.navigate(Screen.ResolutionCenter.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true } // Clear the backstack
                    }
                }
            )
        }
        
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(
                chatId = chatId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId") ?: ""
            BookingScreen(
                businessId = businessId,
                onNavigateBack = { navController.popBackStack() },
                onProceedToPayment = { navController.navigate(Screen.Payment.route) }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Screen.ClientMain.route) {
                        popUpTo(Screen.ClientMain.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ResolutionCenter.route) {
            ResolutionCenterScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubmitReport = { navController.popBackStack() }
            )
        }
    }
}