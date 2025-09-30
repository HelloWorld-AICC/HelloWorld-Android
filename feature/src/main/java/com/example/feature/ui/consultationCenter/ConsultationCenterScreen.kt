package com.example.feature.ui.consultationCenter

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.data.model.Center
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
import com.example.feature.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
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
fun ConsultationCenterScreen(
    onBackClick: () -> Unit,
    viewModel: CenterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var cameraMoved by remember { mutableStateOf(false) }

    val selectedCenter by viewModel.selectedCenter.collectAsState()
    val centerList by viewModel.centerList.collectAsState()

    // ✅ 권한을 FINE/COARSE 둘 다 요청
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // ✅ 둘 중 하나만 승인돼도 위치 기능 활성화
    val hasLocationPermission = remember(locationPermissions.permissions) {
        locationPermissions.permissions.any { it.status.isGranted } ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // ✅ FINE 없으면 BALANCED로 완화
    val locationRequest = remember(hasLocationPermission) {
        val priority =
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                Priority.PRIORITY_HIGH_ACCURACY
            else
                Priority.PRIORITY_BALANCED_POWER_ACCURACY

        LocationRequest.Builder(priority, 10_000L).apply {
            setMinUpdateIntervalMillis(5_000L)
            setMaxUpdateDelayMillis(15_000L)
        }.build()
    }

    // 사용자의 현재 위치가 정해지면 센터 목록을 fetch
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            viewModel.fetchCenterList(
                page = 0,
                size = 20,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    // 위치 콜백 등록 및 해제
    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        // 위도 보정(기존 코드 유지)
                        val latLng = LatLng(location.latitude - 0.006, location.longitude)
                        userLocation = latLng

                        if (!cameraMoved) {
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(latLng, 15f)
                            cameraMoved = true
                        }
                    }
                }
            }

            // ✅ 권한이 있을 때만, 어노테이션이 붙은 헬퍼로 호출
            startLocationUpdatesSafely(
                client = fusedLocationClient,
                request = locationRequest,
                callback = locationCallback,
                looper = context.mainLooper
            )

            onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        } else {
            onDispose { /* no-op */ }
        }
    }

    // 특정 센터를 선택하면 카메라 이동
    LaunchedEffect(selectedCenter) {
        selectedCenter?.let { center ->
            val latLng = LatLng(center.latitude, center.longitude)
            val update = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, 15f)
            )
            cameraPositionState.animate(update)
        }
    }

    // UI 영역
    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(
            title = "오프라인 상담센터",
            onBackClick = { onBackClick() }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 나머지 공간을 지도+오버레이가 채움
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission // ✅ 권한 있을 때만
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = hasLocationPermission // ✅ 버튼도 권한 연동
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
                    .align(Alignment.BottomCenter),
                onClick = { viewModel.selectCenter(it) }
            )
        }
    }
}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
)
private fun startLocationUpdatesSafely(
    client: com.google.android.gms.location.FusedLocationProviderClient,
    request: LocationRequest,
    callback: com.google.android.gms.location.LocationCallback,
    looper: Looper
) {
    try {
        client.requestLocationUpdates(request, callback, looper)
    } catch (se: SecurityException) {
        Log.e("LOC", "SecurityException while starting location updates", se)
    }
}

@Composable
fun ConsultationCenterListOverlay(
    centerList: List<Center>,
    selectedCenter: Center?,
    modifier: Modifier = Modifier,
    onClick: (Center) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
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
                        center = center,
                        onClick = { onClick(center) } // 전달
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
fun ConsultationCenterCard(
    center: Center,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(75.dp)
            .clickable { onClick() } // ← 클릭 이벤트
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
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = center.address, style = AppTypography.label03)
            }

            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFCFE3FD))
    }
}
