package com.example.feature.ui.consultationCenter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.data.CenterInfo
import com.example.core.ui.AppTypography
import com.example.feature.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun ConsultationCenterScreen() {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val centerList = listOf(
        CenterInfo(
            name = "용인시 외국인 복지센터",
            status = "영업 중",
            phone = "010 - 0000 - 0000",
            address = "경기도 용인시 처인구 금령로",
            latitude = 37.582000,
            longitude = 126.926000
        ),
        CenterInfo(
            name = "서대문 외국인 센터",
            status = "영업 종료",
            phone = "010 - 1234 - 5678",
            address = "서울특별시 서대문구 연희로",
            latitude = 37.578500,
            longitude = 126.924500
        ),
        CenterInfo(
            name = "이대 상담소",
            status = "영업 중",
            phone = "02 - 9876 - 5432",
            address = "서울특별시 서대문구 이화여대길",
            latitude = 37.581200,
            longitude = 126.920000
        ),
        CenterInfo(
            name = "신촌 외국인 상담소",
            status = "영업 중",
            phone = "02 - 1234 - 5678",
            address = "서울특별시 서대문구 신촌로",
            latitude = 37.556000,
            longitude = 126.935000
        )
    )

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationRequest = remember {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).apply {
            setMinUpdateIntervalMillis(5000L)
            setMaxUpdateDelayMillis(15000L)
        }.build()
    }

    // 권한 요청
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // 위치 콜백 등록 및 해제
    DisposableEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    result.lastLocation?.let { location ->
                        val latLng = LatLng(location.latitude, location.longitude)
                        userLocation = latLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper
            )

            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } else {
            onDispose {}
        }
    }

    // UI 영역
    Box(Modifier.fillMaxSize()) {


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true
            )
        ) {
            centerList.forEach { center ->
                Marker(
                    state = MarkerState(position = LatLng(center.latitude, center.longitude)),
                    title = center.name
                )
            }
        }

        HeaderTitle(
            modifier = Modifier
                .align(Alignment.TopStart) // 위치 지정
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            ConsultationCenterListOverlay(centerList)
        }
    }
}

@Composable
fun ConsultationCenterListOverlay(centerList: List<CenterInfo>) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(373.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {
        // ▶ 카드 내용 영역
        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 0.dp)
        ) {
            Text(
                text = "내 주변 상담센터",
                style = AppTypography.body01,
                modifier = Modifier.padding(bottom = 3.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .width(281.dp)
                    .heightIn(300.dp)
            ) {
                items(centerList) { center ->
                    ConsultationCenterCard(center)
                }
            }
        }

        // ▶ 하단 그라디언트 덮개 (블러처럼 보이게)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White
                        )
                    )
                )
        )
    }
}


@Composable
fun ConsultationCenterCard(center: CenterInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(75.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = center.name, style = AppTypography.body01)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = center.status,
                        style = AppTypography.label03,
                        color = if (center.status == "영업 중") Color(0xFF5A90D2) else Color(0xFFA6A6A6)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "•", style = AppTypography.label03)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = center.phone, style = AppTypography.label03)
                }
                Spacer(modifier = Modifier.height(4.dp)) // ← 간격 추가

                Text(text = center.address, style = AppTypography.label03)
            }

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFCFE3FD))
    }
}


@Preview
@Composable
fun HeaderTitle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),

        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = "뒤로가기",
            tint = Color.Unspecified,
            modifier = Modifier.padding(end = 12.dp).size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "오프라인 상담센터",
            style = AppTypography.heading04,
            textAlign = TextAlign.Start
        )
    }
}

