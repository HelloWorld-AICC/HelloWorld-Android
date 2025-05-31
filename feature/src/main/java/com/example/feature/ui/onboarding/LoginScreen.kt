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
            val authCode = account.serverAuthCode  // OAuth 방식

            Log.d("LOGIN", "전송할 authCode: $authCode")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.authService.getToken(authCode!!)
                    val atk = response.result.tokenList.firstOrNull { it.types == "atk" }?.token
                    val rtk = response.result.tokenList.firstOrNull { it.types == "rtk" }?.token

                    Log.d("TOKEN", "ATK: $atk")
                    Log.d("TOKEN", "RTK: $rtk")

                    withContext(Dispatchers.Main) {
                        navController.navigate("언어 설정")
                    }
                } catch (e: Exception) {
                    Log.e("TOKEN", "API 연동 실패: ${e.localizedMessage}")
                }
            }
        } catch (e: ApiException) {
            Log.e("LOGIN", "Google sign in failed", e)
        }
    }

    Button(
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestServerAuthCode("283350122061-8gk7hs4eesqrqu6gmjl29okt44221otm.apps.googleusercontent.com", true)
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
