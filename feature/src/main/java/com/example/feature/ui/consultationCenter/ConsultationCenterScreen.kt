package com.example.feature.ui.consultationCenter

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.data.model.Center
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
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
@Composable
fun ConsultationCenterScreen (
    onBackClick: () -> Unit,
    viewModel: CenterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val selectedCenter by viewModel.selectedCenter.collectAsState()
    val centerList by viewModel.centerList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCenterListIfTokenExists()
    }

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
    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(
            title = "오프라인 상담센터",
            onBackClick = { onBackClick() }
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // 나머지 공간을 지도+오버레이가 채움
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true
                ),
                onMapClick = {
                    viewModel.selectCenter(null)
                }
            ) {
                centerList.forEach { center ->
                    val markerState = remember(center) {
                        MarkerState(position = LatLng(center.latitude, center.longitude))
                    }

                    Marker(
                        state = markerState,
                        title = center.name,
                        onClick = {
                            viewModel.selectCenter(center)
                            false
                        }
                    )
                }
            }

            // 지도 위에 오버레이
            ConsultationCenterListOverlay(
                centerList = centerList,
                selectedCenter = selectedCenter,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun ConsultationCenterListOverlay(
    centerList: List<Center>,
    selectedCenter: Center?,
    modifier: Modifier = Modifier
) {

    val targetHeight = if (selectedCenter != null) 373.dp else 200.dp

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        label = "overlayHeight"
    )

    Box(
        modifier = modifier
            .width(320.dp)
            .height(animatedHeight)
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
                    .weight(1f) // 높이 자동 확장
            ) {
                items(centerList) { center ->
                    ConsultationCenterCard(
                        center = center
                    )
                }
            }
        }

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
fun ConsultationCenterCard(center: Center) {
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
//                    Spacer(modifier = Modifier.width(2.dp))
//                    Text(text = center.phone, style = AppTypography.label03)
                }
                Spacer(modifier = Modifier.height(4.dp)) // ← 간격 추가

                Text(text = center.address, style = AppTypography.label03)
            }

            Image(
                painter = painterResource(id = R.drawable.ic_google),
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
