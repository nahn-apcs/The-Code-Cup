package com.example.coffee_application.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.ui.component.BottomBarItem
import com.example.coffee_application.ui.screen.cart.CartScreen
import com.example.coffee_application.ui.screen.details.DetailsScreen
import com.example.coffee_application.ui.screen.main.MainScreen
import com.example.coffee_application.ui.screen.profile.ProfileScreen
import com.example.coffee_application.ui.screen.redeem.RedeemScreen
import com.example.coffee_application.ui.screen.splash.SplashScreen
import com.example.coffee_application.ui.screen.success.OrderSuccessScreen
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.ProfileViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost

private const val DURATION_MILLIS = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    languageViewModel: LanguageViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                languageViewModel = languageViewModel,
                onLoginSuccess = { phone ->
                    profileViewModel.loadProfile(phone)
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main?startTab={startTab}",
            arguments = listOf(
                navArgument("startTab") {
                    type = NavType.StringType
                    defaultValue = BottomBarItem.HOME.name
                }
            ),
            enterTransition = {
                if (initialState.destination.route == "orderSuccess") {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS))
                } else {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS))
                }
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS))
            },
            popExitTransition = {
                if (targetState.destination.route == "orderSuccess") {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS))
                } else {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS))
                }
            }
        ) { backStackEntry ->
            val startTabName = backStackEntry.arguments?.getString("startTab")
            MainScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                languageViewModel = languageViewModel,
                cartViewModel = cartViewModel,
                initialTabName = startTabName ?: BottomBarItem.HOME.name
            )
        }

        composable(
            route = "details/{coffeeName}",
            arguments = listOf(navArgument("coffeeName") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) }
        ) { backStackEntry ->
            val coffeeName = backStackEntry.arguments?.getString("coffeeName") ?: ""
            DetailsScreen(
                coffeeName = coffeeName,
                navController = navController,
                languageViewModel = languageViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(
            route = "profile",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) }
        ) {
            ProfileScreen(
                profileViewModel = profileViewModel,
                navController = navController,
                languageViewModel = languageViewModel,
                cartViewModel = cartViewModel,
            )
        }

        composable(
            route = "cart",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) }
        ) {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                languageViewModel = languageViewModel,
                profileViewModel = profileViewModel
            )
        }

        composable(
            route = "orderSuccess",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) },
            exitTransition = {
                if (targetState.destination.route?.startsWith("main") == true) {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS))
                } else {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS))
                }
            },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) }
        ) {
            OrderSuccessScreen(
                navController = navController,
                languageViewModel = languageViewModel
            )
        }

        composable(
            route = "redeem",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(DURATION_MILLIS)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(DURATION_MILLIS)) }
        ) {
            RedeemScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                languageViewModel = languageViewModel
            )
        }
    }
}