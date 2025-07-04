package com.example.coffee_application.ui.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.coffee_application.HomePageScreen
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.ui.component.BottomBarItem
import com.example.coffee_application.ui.component.BottomNavigationBar
import com.example.coffee_application.ui.screen.order.MyOrderScreen
import com.example.coffee_application.ui.screen.rewards.RewardsScreen
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.ProfileViewModel

@Composable
fun MainScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    languageViewModel: LanguageViewModel,
    cartViewModel: CartViewModel,
    initialTabName: String
) {

    val initialTab = rememberSaveable {
        BottomBarItem.values().find { it.name == initialTabName } ?: BottomBarItem.HOME
    }

    var selectedItem by rememberSaveable { mutableStateOf(initialTab) }


    Scaffold(
        bottomBar  = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { newItem ->
                    selectedItem = newItem
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedItem,
            label = "MainScreenAnimation",
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(300))
                } else {
                    slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(300))
                }
            }
        ) { targetScreen ->
            when (targetScreen) {
                BottomBarItem.HOME -> HomePageScreen(
                    paddingValues = innerPadding,
                    profileViewModel = profileViewModel,
                    languageViewModel = languageViewModel,
                    navController = navController,
                    cartViewModel = cartViewModel
                )
                BottomBarItem.GIFT -> RewardsScreen(
                    paddingValues = innerPadding,
                    profileViewModel = profileViewModel,
                    languageViewModel = languageViewModel,
                    navController = navController
                )
                BottomBarItem.BILL -> MyOrderScreen(
                    paddingValues = innerPadding,
                    navController = navController,
                    profileViewModel = profileViewModel,
                    languageViewModel = languageViewModel
                )
            }
        }
    }
}