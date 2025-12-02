// 로그인 성공 후 토큰 저장 및 화면 전환

package com.example.feature.ui.onboarding.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.core.ui.theme.*
import com.example.feature.R
import com.example.feature.ui.splash.SplashImg
import com.example.feature.ui.splash.SplashLogo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.feature.ui.onboarding.viewmodel.LoginViewModel
import androidx.compose.runtime.getValue
import com.example.core.data.network.RetrofitInstance


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val isExistUser by viewModel.isExistUser.collectAsState()

    RetrofitInstance.init(context) // 앱 시작 시 초기화

    // 로그인 성공 시 화면 전환
    if (loginSuccess) {
        if (isExistUser) {
            navController.navigate("홈") {
                popUpTo("온보딩") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate("언어 설정") {
                popUpTo("스플래시") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HelloWorldMain0)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SplashLogo()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "삶과 일이 이어지는 그 모든 순간을 함께",
                    style = AppTypography.heading04,
                    color = HelloWorldMain500
                )
            }

            GoogleSignInButton { email, accessToken ->
                viewModel.handleGoogleLogin(email, accessToken)
            }

            SplashImg()
        }
    }
}

@Composable
fun GoogleSignInButton(onTokenReceived: (String?, String?) -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email
            val authCode = account?.serverAuthCode
            Log.d("LOGIN", "email: $email, authCode: $authCode")
            onTokenReceived(email, authCode)
        } catch (e: ApiException) {
            Log.e("LOGIN", "Google 로그인 실패", e)
        }
    }

    Button(
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(
                    "283350122061-8gk7hs4eesqrqu6gmjl29okt44221otm.apps.googleusercontent.com"
                )
                .requestEmail()
                .requestProfile()
                .requestServerAuthCode(
                    "283350122061-8gk7hs4eesqrqu6gmjl29okt44221otm.apps.googleusercontent.com",
                    true
                )
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, HelloWorldGoogleBorder),
        modifier = Modifier.height(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Continue with Google",
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            color = HelloWorldGoogleText,
            fontSize = 14.sp
        )
    }
}
