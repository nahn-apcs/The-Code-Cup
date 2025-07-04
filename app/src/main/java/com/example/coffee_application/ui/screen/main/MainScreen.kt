// file: com/example/coffee_application/ui/screen/main/MainScreen.kt

package com.example.coffee_application.ui.screen.main

// Thêm các import cần thiết cho animation

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
        // Sử dụng AnimatedContent để tạo hiệu ứng chuyển cảnh
        AnimatedContent(
            targetState = selectedItem,
            label = "MainScreenAnimation",
            transitionSpec = {
                // Xác định hướng trượt dựa trên thứ tự của item trong enum
                // targetState.ordinal là vị trí của item mới
                // initialState.ordinal là vị trí của item cũ
                if (targetState.ordinal > initialState.ordinal) {
                    // Trượt từ phải sang trái
                    slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(300))
                } else {
                    // Trượt từ trái sang phải
                    slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(300))
                }
            }
        ) { targetScreen ->
            // `targetScreen` chính là `selectedItem` sau khi chuyển đổi
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