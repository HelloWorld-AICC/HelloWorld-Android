package com.example.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.Gray300
import com.example.core.ui.theme.Gray800
import com.example.core.ui.theme.Main100
import com.example.core.ui.theme.Main500
import com.example.core.ui.theme.Pretendard
import com.example.core.ui.theme.White
import com.example.feature.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("대한민국") }
    val languageList = listOf("대한민국", "United States", "日本", "中國")
    var expanded by remember { mutableStateOf(false) }

    // 헤더
    BackHeader(
        title = "",
        onBackClick = {navController.popBackStack()}
    )

    // 뷰
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.language_character),
            contentDescription = "language character",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "어떤 언어가 편하신가요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Pretendard,
            color = Gray800
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "사용할 언어를 선택해 주세요",
            fontSize = 14.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            color = Gray300
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 드롭다운 메뉴
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.background((White))
        ) {
            OutlinedTextField(
                value = selectedLanguage,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Main100,
                    unfocusedBorderColor = Main100,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background((White))
                    .border(1.dp, Main100, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
            ) {
                languageList.forEach { language ->
                    Surface(
                        color = Color.White,
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    language,
                                    fontFamily = Pretendard,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                selectedLanguage = language
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("이용 동의") },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Main500)
        ) {
            Text(
                text = "확인",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
