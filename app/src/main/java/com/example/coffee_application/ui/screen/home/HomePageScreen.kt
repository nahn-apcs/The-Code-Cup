package com.example.coffee_application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.ui.screen.home.BestSellerCarousel
import com.example.coffee_application.ui.screen.home.CoffeeGridSection
import com.example.coffee_application.ui.screen.home.bestSellerItems
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.ProfileViewModel
import java.util.Calendar

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_medium, FontWeight.Medium),
)


@Composable
fun HomePageScreen(
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel,
    languageViewModel: LanguageViewModel,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val currentLang by languageViewModel.language.collectAsState()
    val greeting = getGreetingMessage(currentLang)
    val profile by profileViewModel.profile.collectAsState()
    val displayName = profile?.fullName?.takeIf { it.isNotBlank() } ?: "Guest"
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItemCount = cartItems.sumOf { it.quantity }
    val loyaltyPts = profile?.loyaltyPts ?: 0

    Column(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF324A59),
                            Color(0xFFD7CCC8)
                        )
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 60.dp, bottom = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        fontFamily = Poppins,
                        text = greeting,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFCDD3D5)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        fontFamily = Poppins,
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFECEFF1)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        navController.navigate("profile")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFFECEFF1)
                        )
                    }
                    BadgedBox(
                        badge = {
                            if (totalItemCount > 0) {
                                Badge(modifier = Modifier.offset(x = (-15).dp, y = 5.dp)) {
                                    val badgeText = if (totalItemCount >= 100) "99" else totalItemCount.toString()
                                    Text(text = badgeText)
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = {
                            navController.navigate("cart")
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFECEFF1)
                            )
                        }
                    }
                    Image(
                        painter = painterResource(
                            id = if (currentLang == "vi") R.drawable.ic_flag_uk else R.drawable.ic_flag_vn
                        ),
                        contentDescription = "Language Switch",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                languageViewModel.switchLanguage()
                            }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LoyaltyCard(
                    stampCount = loyaltyPts,
                    language = currentLang,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                BestSellerCarousel(
                    items = bestSellerItems,
                    languageViewModel,
                    navController = navController
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                CoffeeGridSection(
                    language = currentLang,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun LoyaltyCard(
    stampCount: Int = 4,
    maxStamp: Int = 8,
    language: String = "en",
    modifier: Modifier = Modifier
) {
    val cardTitle = if (language == "vi") "Thẻ tích điểm" else "Loyalty card"
    val stamp = stampCount % maxStamp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF324A59), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontFamily = Poppins,
                text = cardTitle,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "$stamp / $maxStamp",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEEEEEE),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(12.dp)
            ) {
                for (i in 1..maxStamp) {
                    val icon = if (i <= stamp) R.drawable.ic_cup_filled else R.drawable.ic_cup_empty
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Stamp $i",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

fun getGreetingMessage(lang: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (lang) {
        "vi" -> when (hour) {
            in 5..11 -> "Chào buổi sáng"
            in 12..17 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
        else -> when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}