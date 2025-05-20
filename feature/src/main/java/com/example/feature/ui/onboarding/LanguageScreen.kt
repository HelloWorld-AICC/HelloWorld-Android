package com.example.feature.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.core.ui.component.BackHeader
import com.example.core.ui.components.BottomButton
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.HelloWorldMain500
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
        onBackClick = { navController.popBackStack() }
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
            style = AppTypography.heading01,
            color = HelloWorldGrayScale800
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "사용할 언어를 선택해 주세요",
            style = AppTypography.body02,
            color = HelloWorldGrayScale300
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
                    focusedBorderColor = HelloWorldMain100,
                    unfocusedBorderColor = HelloWorldMain100,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background((White))
                    .border(1.dp, HelloWorldMain100, RoundedCornerShape(8.dp)),
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
                                    style = AppTypography.heading04
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
        BottomButton(
            text = "확인",
            onClick = { navController.navigate("이용 동의") },
            enabled = true,
            modifier = Modifier
        )
    }
}
