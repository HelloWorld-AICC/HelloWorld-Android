// com/example/feature/ui/consultationCenter/ConsultationCenterScreen.kt
package com.example.feature.ui.consultationCenter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.component.BackHeader
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.HelloWorldMain700
import com.example.feature.R
import com.example.model.common.Language
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
import com.example.core.ui.R as languageR

private const val CAMERA_LAT_SHIFT = -0.006          // ?ㅻ쾭?덉씠 蹂댁젙
private const val REQUERY_THRESHOLD_METERS = 5_000f  // 吏??以묒떖 ?대룞 ?꾧퀎(5km)

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

    // ✅ UI는 DisplayCenter만 사용
    val selectedCenter by viewModel.selectedDisplayCenter.collectAsState()
    val selectedCenterId by viewModel.selectedCenterId.collectAsState()
    val centerList by viewModel.displayCenterList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ✅ 언어(시/구/동 + 영업중/종료 텍스트 반영에 사용)
    val language by viewModel.language.collectAsState()

    val listState = rememberLazyListState()
    var locationTitle by remember { mutableStateOf("") }

    // 지도 기반 재조회 기준점(마지막 쿼리 중심)
    var lastQueryLocation by remember { mutableStateOf<LatLng?>(null) }

    // ✅ 위치 → 행정구역명 갱신 (언어 변경 시에도 재계산)
    LaunchedEffect(userLocation, language) {
        userLocation?.let { ll ->
            val title = reverseGeocodeToSidoGu(context, ll, language)
            locationTitle = title.orEmpty()
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

    // 최초/의미있는 사용자 위치 변경 시 초기 로드
    LaunchedEffect(userLocation) {
        userLocation?.let { current ->
            val prev = lastQueryLocation
            if (prev == null) {
                lastQueryLocation = current
                viewModel.resetAndLoad(current.latitude, current.longitude)
            } else {
                val dist = FloatArray(1)
                Location.distanceBetween(prev.latitude, prev.longitude, current.latitude, current.longitude, dist)
                if (dist[0] >= REQUERY_THRESHOLD_METERS) {
                    lastQueryLocation = current
                    viewModel.resetAndLoad(current.latitude, current.longitude)
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

    // 센터 선택 시 카메라 이동 (DisplayCenter)
    LaunchedEffect(selectedCenter) {
        selectedCenter?.let { center ->
            val raw = LatLng(center.latitude, center.longitude)
            val corrected = correctedForOverlay(raw)
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(corrected, 15f)
                )
            )
        }
    }

    // UI
    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(title = stringResource(languageR.string.offline_center_nearby), onBackClick = onBackClick)

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
                // ✅ 지도 이동 후(카메라 멈춤) 중심이 5km 이상 바뀌면 재조회
                MapEffect(lastQueryLocation) { map ->
                    map.setOnCameraIdleListener {
                        val target = map.cameraPosition.target
                        val prev = lastQueryLocation
                        if (prev == null) {
                            lastQueryLocation = target
                            viewModel.resetAndLoad(target.latitude, target.longitude)
                        } else {
                            val d = FloatArray(1)
                            Location.distanceBetween(
                                prev.latitude, prev.longitude,
                                target.latitude, target.longitude, d
                            )
                            if (d[0] >= REQUERY_THRESHOLD_METERS) {
                                lastQueryLocation = target
                                viewModel.resetAndLoad(target.latitude, target.longitude)
                            }
                        }
                    }
                }

                // ✅ 내 위치 버튼 보정
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

                // ✅ 마커 (DisplayCenter)
                centerList.forEach { center ->
                    val markerState = remember(center.centerId) {
                        MarkerState(position = LatLng(center.latitude, center.longitude))
                    }
                    Marker(
                        state = markerState,
                        title = center.name,
                        onClick = {
                            viewModel.selectCenter(center.centerId)
                            false
                        }
                    )
                }
            }

            ConsultationCenterListOverlay(
                centerList = centerList,
                selectedCenterId = selectedCenterId,
                headerTitle = locationTitle,
                listState = listState,
                isLoading = isLoading,
                language = language,
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { viewModel.selectCenter(it) }
            )
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
    centerList: List<CenterViewModel.CenterDisplay>,
    selectedCenterId: Int?,
    headerTitle: String,
    listState: LazyListState,
    isLoading: Boolean,
    language: Language,
    modifier: Modifier = Modifier,
    onClick: (Int?) -> Unit
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
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                itemsIndexed(
                    items = centerList,
                    key = { _, item -> item.centerId }
                ) { index, center ->
                    Spacer(modifier = Modifier.height(12.dp))

                    ConsultationCenterCard(
                        center = center,
                        isSelected = center.centerId == selectedCenterId,
                        language = language,
                        onClick = { onClick(center.centerId) }
                    )

                    if (index != centerList.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFCFE3FD))
                    }
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
    center: CenterViewModel.CenterDisplay,
    isSelected: Boolean,
    language: Language,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFF3F8FF) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = center.name, style = AppTypography.body01)
                Spacer(modifier = Modifier.height(4.dp))

                if(center.isOpenNow) {
                    Row{
                        Image(
                            painter = painterResource(id = R.drawable.alarm),
                            modifier = Modifier.size(10.dp),
                            contentDescription = "알람"
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if(center.isOpenNow) {
                        Image(
                            painter = painterResource(id = R.drawable.call),
                            modifier = Modifier.size(10.dp),
                            contentDescription = "전화"
                        )
                    } else {
                        Text(
                            text = stringResource(languageR.string.offline_center_closed),
                            style = AppTypography.label03,
                            color = Color(0xFF6A6A6A),
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "•",
                            style = AppTypography.label03,
                            color = Color(0xFF6A6A6A),
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = center.phoneNumber,
                        style = AppTypography.label03,
                        color = Color(0xFF6A6A6A),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = center.address, style = AppTypography.label03, color = Color(0xFF6A6A6A))
            }
        }
    }
}

// ----------------- reverse geocode (언어 반영) -----------------

private fun languageToLocale(language: Language): Locale = when (language) {
    Language.KOREAN -> Locale.KOREA
    Language.ENGLISH -> Locale.US
    Language.JAPANESE -> Locale.JAPAN
    Language.CHINESE -> Locale.SIMPLIFIED_CHINESE   // zh-CN
    Language.VIETNAMESE -> Locale("vi", "VN")
}

private fun normalizeSido(admin: String?): String {
    if (admin.isNullOrBlank()) return ""
    return admin
        .replace("특별시", "시")
        .replace("광역시", "시")
        .replace("특별자치시", "시")
        .replace("특별자치도", "도")
}

private fun formatAdminArea(a: Address, language: Language): String? {
    val admin = a.adminArea
    val subAdmin = a.subAdminArea
    val locality = a.locality
    val subLocality = a.subLocality

    // ✅ 한국어: 기존 포맷 유지 ("서울시 강남구" / "경기도 수원시" 등)
    if (language == Language.KOREAN) {
        val sido = normalizeSido(admin)
        val guOrDong =
            subAdmin
                ?: locality
                ?: subLocality
                ?: a.thoroughfare
                ?: a.subThoroughfare
                ?: a.featureName

        return if (sido.isNotBlank() && !guOrDong.isNullOrBlank()) "$sido $guOrDong" else null
    }

    // ✅ 그 외 언어: Geocoder가 주는 표현 기반으로 자연스럽게 조합
    val part1 = admin ?: return null
    val part2 = subAdmin ?: locality ?: subLocality
    return if (!part2.isNullOrBlank()) "$part2, $part1" else part1
}

suspend fun reverseGeocodeToSidoGu(
    context: Context,
    latLng: LatLng,
    language: Language
): String? {
    val geocoder = Geocoder(context, languageToLocale(language))
    val lat = latLng.latitude
    val lng = latLng.longitude

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lng, 1) { list ->
                val a = list.firstOrNull()
                if (a == null) {
                    cont.resume(null)
                    return@getFromLocation
                }
                cont.resume(formatAdminArea(a, language))
            }
        }
    } else {
        withContext(Dispatchers.IO) {
            try {
                val a = geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() ?: return@withContext null
                formatAdminArea(a, language)
            } catch (_: Exception) {
                null
            }
        }
    }
}
