package com.example.coffee_application.ui.screen.redeem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    var showDialog by remember { mutableStateOf<Boolean?>(null) }

    val currentPoints = profile?.points ?: 0

    val screenTitle = if (currentLang == "vi") "Đổi quà" else "Redeem"
    val validUntilText = if (currentLang == "vi") "Có giá trị đến" else "Valid until"
    val redeemSuccessTitle = if (currentLang == "vi") "Thành công" else "Success"
    val redeemSuccessMessage = if (currentLang == "vi") "Bạn đã đổi quà thành công!" else "You have successfully redeemed the item!"
    val redeemFailedTitle = if (currentLang == "vi") "Thất bại" else "Failed"
    val redeemFailedMessage = if (currentLang == "vi") "Đổi quà thất bại, vui lòng thử lại." else "Redeem failed, please try again."
    val okButtonText = if (currentLang == "vi") "Đồng ý" else "OK"


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
                            showDialog = success
                        }
                    }
                )
            }
        }

        showDialog?.let { isSuccess ->
            RedemptionStatusDialog(
                isSuccess = isSuccess,
                title = if (isSuccess) redeemSuccessTitle else redeemFailedTitle,
                message = if (isSuccess) redeemSuccessMessage else redeemFailedMessage,
                buttonText = okButtonText,
                onDismiss = { showDialog = null }
            )
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

@Composable
fun RedemptionStatusDialog(
    isSuccess: Boolean,
    title: String,
    message: String,
    buttonText: String,
    onDismiss: () -> Unit
) {
    val icon: ImageVector
    val iconColor: Color

    if (isSuccess) {
        icon = Icons.Filled.CheckCircle
        iconColor = Color(0xFF4CAF50)
    } else {
        icon = Icons.Filled.Error
        iconColor = Color(0xFFF44336)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF324A59)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buttonText,
                        fontFamily = Poppins,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}