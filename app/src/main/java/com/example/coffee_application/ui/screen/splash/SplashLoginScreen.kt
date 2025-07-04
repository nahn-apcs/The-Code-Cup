package com.example.coffee_application.ui.screen.splash

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffee_application.LanguageViewModel
import com.example.coffee_application.R
import com.example.coffee_application.manager.PhoneProfileManager
import com.example.coffee_application.model.Profile
import kotlinx.coroutines.delay

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_medium, FontWeight.Medium),
)


@Composable
fun SplashScreen(
    languageViewModel: LanguageViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val currentLang by languageViewModel.language.collectAsState()
    val keyboardOpen = isKeyboardOpen()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(
                id = if (currentLang == "vi") R.drawable.ic_flag_uk else R.drawable.ic_flag_vn
            ),
            contentDescription = "Language Switch",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 16.dp)
                .size(40.dp)
                .clickable {
                    languageViewModel.switchLanguage()
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.brand_name),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = Color(0xFFFFFAF6),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            ExpandingLoginButton(onLoginSuccess)
        }
    }
}

@Composable
fun isKeyboardOpen(): Boolean {
    val ime = WindowInsets.ime
    return ime.getBottom(LocalDensity.current) > 0
}

@Composable
fun ExpandingLoginButton(onLoginSuccess: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    val context = LocalContext.current
    val profileManager = remember { PhoneProfileManager() }

    val animatedWidth by animateDpAsState(
        targetValue = if (expanded) 280.dp else 150.dp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "buttonWidth"
    )

    LaunchedEffect(expanded) {
        if (expanded) {
            delay(350)
            showContent = true
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!expanded) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.width(150.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A7E72),
                    contentColor = Color(0xFFFDFCFB)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        } else {
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(animatedWidth)
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (showContent) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.enter_phone),
                                    fontFamily = Poppins,
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = Poppins),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                cursorColor = Color.Black,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF8A7E72), shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    if (phoneNumber.length == 10) {
                                        profileManager.isPhoneTaken(phoneNumber) { exists ->
                                            if (exists) {
                                                onLoginSuccess(phoneNumber)
                                            } else {
                                                val newProfile = Profile(phoneNumber = phoneNumber)
                                                profileManager.saveProfile(newProfile) { success ->
                                                    if (success) {
                                                        onLoginSuccess(phoneNumber)
                                                    } else {
                                                        Toast.makeText(context, "Lỗi khi tạo tài khoản", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Vui lòng nhập đủ 10 số", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    animationSpec = tween(durationMillis = 500),
                    initialOffsetY = { fullHeight -> fullHeight }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(durationMillis = 300),
                    targetOffsetY = { fullHeight -> fullHeight }
                )
            ) {
                NumericKeypad { key ->
                    when (key) {
                        "Del" -> phoneNumber = phoneNumber.dropLast(1)
                        else -> if (phoneNumber.length < 10) phoneNumber += key
                    }
                }
            }
        }
    }
}

@Composable
fun NumericKeypad(onKeyClick: (String) -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color(0xFFECE5DF), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    Button(
                        onClick = { onKeyClick(key) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBFAEA0),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = key,
                            fontFamily = Poppins,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onKeyClick("0") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBFAEA0),
                    contentColor = Color.White
                )
            ) {
                Text("0", fontSize = 22.sp, fontFamily = Poppins)
            }

            Button(
                onClick = { onKeyClick("Del") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA45D5D),
                    contentColor = Color.White
                )
            ) {
                Text("Del", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
            }
        }
    }
}