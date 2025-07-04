package com.example.coffee_application.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.Poppins
import com.example.coffee_application.R
import com.example.coffee_application.viewmodel.CartViewModel
import com.example.coffee_application.viewmodel.ProfileViewModel

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_medium, FontWeight.Medium),
)

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
    languageViewModel: LanguageViewModel,
    cartViewModel: CartViewModel,
) {
    val profile by profileViewModel.profile.collectAsState()
    val currentLang by languageViewModel.language.collectAsState()

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editableFullName by rememberSaveable { mutableStateOf("") }
    var editableEmail by rememberSaveable { mutableStateOf("") }
    var editableAddress by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            editableFullName = it.fullName
            editableEmail = it.email
            editableAddress = it.address
        }
    }

    val title = if (currentLang == "vi") "Thông tin" else "Profile"
    val fullNameLabel = if (currentLang == "vi") "Họ tên" else "Full name"
    val phoneLabel = if (currentLang == "vi") "Số điện thoại" else "Phone number"
    val emailLabel = if (currentLang == "vi") "Email" else "Email"
    val addressLabel = if (currentLang == "vi") "Địa chỉ" else "Address"
    val signOutLabel = if (currentLang == "vi") "Đăng xuất" else "Sign Out"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .clickable {
                        if (isEditing) isEditing = false
                        else navController.popBackStack()
                    }
            )

            Text(
                text = title,
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3E2723),
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(id = if (isEditing) R.drawable.ic_save else R.drawable.ic_edit),
                contentDescription = if (isEditing) "Save" else "Edit",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
                    .clickable {
                        if (isEditing) {
                            profileViewModel.updateProfile(
                                fullName = editableFullName,
                                email = editableEmail,
                                address = editableAddress
                            ) { success ->
                                if (success) isEditing = false
                            }
                        } else {
                            isEditing = true
                        }
                    }
            )
        }

        profile?.let {
            ProfileItem(
                icon = R.drawable.ic_name,
                label = fullNameLabel,
                value = editableFullName,
                isEditing = isEditing,
                onValueChange = { editableFullName = it }
            )
            ProfileItem(
                icon = R.drawable.ic_phone,
                label = phoneLabel,
                value = "+${it.phoneNumber}",
                isEditing = false,
                isEditable = false,
                onValueChange = {}
            )
            ProfileItem(
                icon = R.drawable.ic_email,
                label = emailLabel,
                value = editableEmail,
                isEditing = isEditing,
                onValueChange = { editableEmail = it }
            )
            ProfileItem(
                icon = R.drawable.ic_location,
                label = addressLabel,
                value = editableAddress,
                isEditing = isEditing,
                onValueChange = { editableAddress = it }
            )

            Button(
                onClick = {
                    profileViewModel.onSignOut()
                    cartViewModel.clearCart()

                    navController.navigate("splash") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = signOutLabel,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ProfileItem(
    icon: Int,
    label: String,
    value: String,
    isEditing: Boolean,
    isEditable: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F3F6),
            modifier = Modifier.size(56.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.padding(7.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                fontFamily = Poppins,
                text = label,
                color = Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )

            if (isEditing && isEditable) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Text(
                    fontFamily = Poppins,
                    text = value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}