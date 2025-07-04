package com.example.coffee_application.ui.screen.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.R
import com.example.coffee_application.model.Order
import com.example.coffee_application.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel = viewModel(),
    languageViewModel: LanguageViewModel = (viewModel()),
    navController: NavController
) {
    val profile by profileViewModel.profile.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()

    val rewardHistory = profile?.history?.hist ?: emptyList()

    val screenTitle = if (currentLang == "vi") "Phần thưởng" else "Rewards"
    val loyaltyCardTitle = if (currentLang == "vi") "Thẻ tích điểm" else "Loyalty card"
    val myPointsTitle = if (currentLang == "vi") "Điểm của tôi:" else "My Points:"
    val redeemButtonText = if (currentLang == "vi") "Đổi quà" else "Redeem drinks"
    val historyTitle = if (currentLang == "vi") "Lịch sử Phần thưởng" else "History Rewards"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = screenTitle,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(bottom = 30.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                LoyaltyCard(
                    title = loyaltyCardTitle,
                    currentStamps = (profile?.loyaltyPts ?: 0) % 8,
                    totalStamps = 8
                )
            }
            item {
                MyPointsCardImageBackground(
                    title = myPointsTitle,
                    points = profile?.points ?: 0, //
                    buttonText = redeemButtonText,
                    onRedeemClick = {
                        navController.navigate("redeem")
                    }
                )
            }
            item {
                Text(
                    text = historyTitle,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }
            items(rewardHistory) { order ->
                RewardHistoryListItem(item = order)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun LoyaltyCard(title: String, currentStamps: Int, totalStamps: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF324A59)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$currentStamps / $totalStamps",
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..totalStamps) {
                    val iconRes = if (i <= currentStamps) R.drawable.ic_cup_filled else R.drawable.ic_cup_empty
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Stamp $i",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
fun MyPointsCardImageBackground(
    title: String,
    points: Int,
    buttonText: String,
    onRedeemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.point_card),
            contentDescription = "My Points Card",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = points.toString(),
                    color = Color.White,
                    fontFamily = Poppins,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onRedeemClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A606D)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(buttonText, color = Color.White, fontFamily = Poppins, fontSize = 14.sp)
            }
        }
    }
}


@Composable
fun RewardHistoryListItem(item: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = item.name,
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.date.replace(" ", " | "),
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "+ ${item.rewardPoints} Pts",
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF324A59)
        )
    }
}