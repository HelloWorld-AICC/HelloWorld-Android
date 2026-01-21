package com.example.feature.ui.onboarding.screen

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.core.ui.components.BottomButton
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldGrayScale300
import com.example.core.ui.theme.HelloWorldGrayScale800
import com.example.core.ui.theme.HelloWorldMain100
import com.example.core.ui.theme.White
import com.example.feature.ui.onboarding.viewmodel.LanguageViewModel
import com.example.model.common.Language
import com.example.core.ui.R
import com.example.core.ui.component.HWDropdownMenuBox
import com.example.model.common.National

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    navController: NavController,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    var selectedLanguage by remember { mutableStateOf(Language.KOREAN) } // 기본값

    var expanded by remember { mutableStateOf(false) }

    val editingLanguage by viewModel.editingLanguage.collectAsState()
    val languages: List<Language> = Language.entries


    Scaffold(
        bottomBar = {
            BottomButton(
                text = stringResource(R.string.confirm),
                onClick = {
                    viewModel.setLanguage(selectedLanguage) // 언어 설정
                    navController.navigate("이용 동의") {
                        popUpTo("언어 설정") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                enabled = true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                text = stringResource(R.string.auth_language_title),
                style = AppTypography.heading01,
                color = HelloWorldGrayScale800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_language_subtitle),
                style = AppTypography.body02,
                color = HelloWorldGrayScale300
            )

            Spacer(modifier = Modifier.height(32.dp))

            HWDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedItem = if (editingLanguage == null) Language.ENGLISH else editingLanguage,
                items = languages,
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                onClick = {
                    viewModel.updateLanguage(it)
                    expanded = false
                },
                selectedContent = { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = when (item?.national) {
                                National.KOREAN -> painterResource(R.drawable.flag_kr)
                                National.JAPANESE -> painterResource(R.drawable.flag_jp)
                                National.CHINESE -> painterResource(R.drawable.flag_ch)
                                National.VIETNAMESE -> painterResource(R.drawable.flag_vi)
                                else -> painterResource(R.drawable.flag_en)
                            },
                            contentDescription = "국기",
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = "${item?.displayName}",
                            style = AppTypography.heading04,
                            color = HelloWorldGrayScale800,
                        )
                    }
                },
                itemContent = { item, isSelected ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = when (item.national) {
                                National.KOREAN -> painterResource(R.drawable.flag_kr)
                                National.JAPANESE -> painterResource(R.drawable.flag_jp)
                                National.CHINESE -> painterResource(R.drawable.flag_ch)
                                National.VIETNAMESE -> painterResource(R.drawable.flag_vi)
                                else -> painterResource(R.drawable.flag_en)
                            },
                            contentDescription = "국기",
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = item.displayName,
                            style = AppTypography.heading04,
                            color = HelloWorldGrayScale800,
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
