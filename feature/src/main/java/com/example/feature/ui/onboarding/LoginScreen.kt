// 로그인 성공 후 토큰 저장 및 화면 전환

package com.example.feature.ui.onboarding

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.core.ui.theme.*
import com.example.feature.R
import com.example.feature.ui.splash.SplashImg
import com.example.feature.ui.splash.SplashLogo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.core.data.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    RetrofitInstance.init(context) // 앱 시작 시 초기화

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

            GoogleSignInButton(navController = navController)

            SplashImg()
        }
    }
}

@Composable
fun GoogleSignInButton(navController: NavController) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            Log.d("LOGIN", "idToken: $idToken")

            if (idToken != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.authService.getToken(idToken)

                        val tokenList = response.result?.tokenList
                        val atk = tokenList?.firstOrNull { it.types == "ATK" }?.token
                        val rtk = tokenList?.firstOrNull { it.types == "RTK" }?.token

                        if(!atk.isNullOrBlank()) {
                            // AccessToken 설정 (Retrofit 재생성 포함)
                            RetrofitInstance.setAccessToken(atk)
                            Log.d("LOGIN", "ATK 설정 성공: $atk")

                            withContext(Dispatchers.Main) {
                                // ATK 설정 완료된 후에 홈 화면으로 이동
                                navController.navigate("언어 설정") {
                                    popUpTo("Login") { inclusive = true }
                                }
                            }
                        } else {
                            Log.e("LOGIN", "ATK가 비어 있거나 없음")
                        }
                    } catch (e: Exception) {
                        Log.e("LOGIN", "토큰 요청 실패", e)
                    }
                }
            } else {
                Log.e("LOGIN", "idToken이 null입니다")
            }
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
