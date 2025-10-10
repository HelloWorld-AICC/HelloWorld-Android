package com.example.feature.ui.consultationCenter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.data.model.Center
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldMain700
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
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
private const val CAMERA_LAT_SHIFT = -0.006  // 초기 보정과 동일

private fun correctedForOverlay(latLng: LatLng, shift: Double = CAMERA_LAT_SHIFT): LatLng =
    LatLng(latLng.latitude + shift, latLng.longitude)

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

    var locationTitle by remember { mutableStateOf("") }

    // userLocation이 설정될 때 주소로 갱신
    LaunchedEffect(userLocation) {
        userLocation?.let { ll ->
            reverseGeocodeToSidoGu(context, ll)?.let { sidoGu ->
                locationTitle = sidoGu   // 예: "서울시 구로구"
            }
        }
    }

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
                        val raw = LatLng(location.latitude, location.longitude)
                        val corrected = correctedForOverlay(raw)   // 🔁 공통 헬퍼 사용
                        userLocation = corrected

                        if (!cameraMoved) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(corrected, 15f)
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
            val raw = LatLng(center.latitude, center.longitude)
            val corrected = correctedForOverlay(raw)   // 🔁 동일 보정
            val update = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(corrected, 15f)
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
                MapEffect(userLocation) { map ->
                    map.setOnMyLocationButtonClickListener {
                        val target = userLocation
                        if (target != null) {
                            val update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(target, 15f) // 줌은 기존과 동일
                            )
                            map.animateCamera(update)   // 기본 동작 대신 우리가 보정 반영한 좌표로 이동
                            true                       // 이벤트 소비(기본 recenter 막기)
                        } else {
                            false                      // 위치 모르면 기본 동작 실행
                        }
                    }
                }

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
                headerTitle = locationTitle,          // ✅ 추가
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
    headerTitle: String,
    modifier: Modifier = Modifier,
    onClick: (Center) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color.White)
    ) {
        // ⬇️ 패딩 포함 컨텐트 래퍼
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {
            // ⬇️ 아이콘 + 현재 위치(구 단위)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = "현재 위치",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = headerTitle.ifBlank {""},
                    style = AppTypography.body01,
                    maxLines = 1,
                    color = HelloWorldMain700,              // ✅ 원하는 색
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ⬇️ 리스트 (패딩 영역과 동일 폭)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                // 하단 그라디언트(60dp)에 가리지 않도록 여유 패딩
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(centerList) { center ->
                    ConsultationCenterCard(center = center) { onClick(center) }
                }
            }
        }

        // ⬇️ 하단 그라디언트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White)
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
            .height(87.dp)
            .clickable { onClick() } // ← 클릭 이벤트
    ) {
        Spacer(modifier = Modifier.height(12.dp))
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
                        text = if (center.status == "OPEN") "영업 중" else "영업 종료",
                        style = AppTypography.label03,
                        color = if (center.status == "OPEN") Color(0xFF5A90D2) else Color(0xFFA6A6A6)
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

private fun normalizeSido(admin: String?): String {
    if (admin.isNullOrBlank()) return ""
    // “서울특별시” → “서울시”, “부산광역시” → “부산시” 등
    return admin
        .replace("특별시", "시")
        .replace("광역시", "시")
        .replace("특별자치시", "시")
        .replace("특별자치도", "도")
}

suspend fun reverseGeocodeToSidoGu(
    context: Context,
    latLng: LatLng
): String? {
    val geocoder = Geocoder(context, Locale.KOREA)
    val lat = latLng.latitude
    val lng = latLng.longitude

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lng, 1) { list ->
                val a = list.firstOrNull()
                val sido = normalizeSido(a?.adminArea)              // 서울시/부산시…
                val gu = a?.locality ?: a?.subLocality ?: a?.subAdminArea // 구로구/영등포구…
                cont.resume(
                    if (!sido.isNullOrBlank() && !gu.isNullOrBlank())
                        "$sido $gu" else null
                )
            }
        }
    } else {
        withContext(Dispatchers.IO) {
            try {
                val a = geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()
                val sido = normalizeSido(a?.adminArea)
                val gu = a?.locality ?: a?.subLocality ?: a?.subAdminArea
                if (!sido.isNullOrBlank() && !gu.isNullOrBlank()) "$sido $gu" else null
            } catch (_: Exception) {
                null
            }
        }
    }
}