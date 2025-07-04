package com.example.coffee_application.ui.screen.redeem

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.data.CoffeeItem
import com.example.coffee_application.data.coffeeOptions
import com.example.coffee_application.viewmodel.ProfileViewModel

private const val REDEMPTION_COST = 120

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    languageViewModel: LanguageViewModel = viewModel()
) {
    val profile by profileViewModel.profile.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()
    val context = LocalContext.current

    val currentPoints = profile?.points ?: 0

    val screenTitle = if (currentLang == "vi") "Đổi quà" else "Redeem"
    val validUntilText = if (currentLang == "vi") "Có giá trị đến" else "Valid until"
    val redeemSuccessText = if (currentLang == "vi") "Đổi quà thành công!" else "Redeem successful!"
    val redeemFailedText = if (currentLang == "vi") "Đổi quà thất bại, vui lòng thử lại." else "Redeem failed, please try again."

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle, fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(coffeeOptions) { coffeeItem ->
                RedeemItem(
                    coffee = coffeeItem,
                    currentUserPoints = currentPoints,
                    validUntilText = validUntilText,
                    onRedeemClick = {
                        profileViewModel.redeemDrink(coffeeItem, REDEMPTION_COST) { success ->
                            if (success) {
                                Toast.makeText(context, redeemSuccessText, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, redeemFailedText, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RedeemItem(
    coffee: CoffeeItem,
    currentUserPoints: Int,
    validUntilText: String,
    onRedeemClick: () -> Unit
) {
    val canRedeem = currentUserPoints >= REDEMPTION_COST

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = coffee.imageRes),
                contentDescription = coffee.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = coffee.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = "$validUntilText 04.07.21",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Button(
            onClick = onRedeemClick,
            enabled = canRedeem,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF324A59),
                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = "$REDEMPTION_COST pts",
                fontFamily = Poppins,
                color = Color.White
            )
        }
    }
}