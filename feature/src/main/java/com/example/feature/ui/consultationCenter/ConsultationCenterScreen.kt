// com/example/feature/ui/consultationCenter/ConsultationCenterScreen.kt
package com.example.feature.ui.consultationCenter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

private const val CAMERA_LAT_SHIFT = -0.006          // 오버레이 보정
private const val QUERY_RADIUS_METERS = 100_000f     // 🔵 반경 100km
private const val REQUERY_THRESHOLD_METERS = 500f  // 지도 중심 이동 임계(500m)

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
    val isLoading by viewModel.isLoading.collectAsState()

    val listState = rememberLazyListState()

    var locationTitle by remember { mutableStateOf("") }

    // 지도 기반 재조회 기준점(마지막 쿼리 중심)
    var lastQueryLocation by remember { mutableStateOf<LatLng?>(null) }

    // 위치 → 행정구역명 갱신
    LaunchedEffect(userLocation) {
        userLocation?.let { ll ->
            reverseGeocodeToSidoGu(context, ll)?.let { sidoGu ->
                locationTitle = sidoGu
            }
        }
    }

    // 위치 권한
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val hasLocationPermission =
        locationPermissions.permissions.any { it.status.isGranted } ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

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

    // 최초/의미있는 사용자 위치 변경 시 100km 반경으로 초기 로드
    LaunchedEffect(userLocation) {
        userLocation?.let { current ->
            val prev = lastQueryLocation
            if (prev == null) {
                lastQueryLocation = current
                viewModel.resetAndLoad(
                    latitude = current.latitude,
                    longitude = current.longitude,
                    radiusMeters = QUERY_RADIUS_METERS
                )
            } else {
                val dist = FloatArray(1)
                Location.distanceBetween(prev.latitude, prev.longitude, current.latitude, current.longitude, dist)
                if (dist[0] >= REQUERY_THRESHOLD_METERS) {
                    lastQueryLocation = current
                    viewModel.resetAndLoad(
                        latitude = current.latitude,
                        longitude = current.longitude,
                        radiusMeters = QUERY_RADIUS_METERS
                    )
                }
            }
        }
    }

    // 위치 업데이트
    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        val raw = LatLng(location.latitude, location.longitude)
                        val corrected = correctedForOverlay(raw)
                        userLocation = corrected

                        if (!cameraMoved) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(corrected, 15f)
                            cameraMoved = true
                        }
                    }
                }
            }

            startLocationUpdatesSafely(
                client = fusedLocationClient,
                request = locationRequest,
                callback = locationCallback,
                looper = context.mainLooper
            )
            onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        } else onDispose { }
    }

    // 센터 선택 시 카메라 이동
    LaunchedEffect(selectedCenter) {
        selectedCenter?.let { center ->
            val raw = LatLng(center.latitude, center.longitude)
            val corrected = correctedForOverlay(raw)
            cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(corrected, 15f)
            ))
        }
    }

    // 바닥 스크롤 감지 → 다음 페이지 로드
    LaunchedEffect(listState, centerList) {
        snapshotFlow {
            val total = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            total > 0 && lastVisible >= total - 1
        }.distinctUntilChanged()
            .collectLatest { reachedEnd ->
                if (reachedEnd) viewModel.loadNextPage()
            }
    }

    // UI
    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(title = "오프라인 상담센터", onBackClick = onBackClick)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission),
                onMapClick = { viewModel.selectCenter(null) }
            ) {
                // ✅ 지도 이동 후(카메라 멈춤) 중심 기준 반경 100km 재조회
                MapEffect(lastQueryLocation) { map ->
                    map.setOnCameraIdleListener {
                        val target = map.cameraPosition.target
                        val prev = lastQueryLocation
                        if (prev == null) {
                            lastQueryLocation = target
                            viewModel.resetAndLoad(
                                target.latitude, target.longitude, QUERY_RADIUS_METERS
                            )
                        } else {
                            val d = FloatArray(1)
                            Location.distanceBetween(prev.latitude, prev.longitude,
                                target.latitude, target.longitude, d)
                            if (d[0] >= REQUERY_THRESHOLD_METERS) {
                                lastQueryLocation = target
                                viewModel.resetAndLoad(
                                    target.latitude, target.longitude, QUERY_RADIUS_METERS
                                )
                            }
                        }
                    }
                }

                // ✅ 내 위치 버튼 보정(이것도 반드시 GoogleMap 내부)
                MapEffect(userLocation) { m ->
                    m.setOnMyLocationButtonClickListener {
                        val target = userLocation
                        if (target != null) {
                            m.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(target, 15f)
                                )
                            )
                            true
                        } else false
                    }
                }

                // 마커 렌더링
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


            ConsultationCenterListOverlay(
                centerList = centerList,
                selectedCenter = selectedCenter,
                headerTitle = locationTitle,
                listState = listState,
                isLoading = isLoading,
                modifier = Modifier.align(Alignment.BottomCenter),
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
    listState: LazyListState,
    isLoading: Boolean,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {
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
                    text = headerTitle.ifBlank { "" },
                    style = AppTypography.body01,
                    maxLines = 1,
                    color = HelloWorldMain700,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 72.dp)
            ) {
                items(centerList) { center ->
                    ConsultationCenterCard(center = center) { onClick(center) }
                }

                item {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
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
            .clickable { onClick() }
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
                val sido = normalizeSido(a?.adminArea)
                val gu = a?.locality ?: a?.subLocality ?: a?.subAdminArea
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
