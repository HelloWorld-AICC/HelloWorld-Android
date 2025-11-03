package com.example.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberPhotoPickerWithPermission(
    onImageSelected: (Uri) -> Unit
): () -> Unit {
    val context = LocalContext.current

    // Photo Picker 런처
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { contentUri ->
        if (contentUri != null) {
            onImageSelected(contentUri)
        }
    }

    // 권한 요청 런처 (권한 승인 시 자동으로 photo picker 실행)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Android 13+ 에서 "일부 제한된 액세스 허용" 선택 시 isGranted=false이지만
        // photo picker는 실행되어야 하므로 권한 체크 없이 실행
        photoPicker.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    // 권한 체크 및 Photo Picker 실행 함수 반환
    return {
        checkAndRequestPhotoPermission(
            context = context,
            onPermissionGranted = {
                photoPicker.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            onRequestPermission = { permission ->
                permissionLauncher.launch(permission)
            }
        )
    }
}

@Composable
fun rememberMultiplePhotoPickerWithPermission(
    maxItems: Int = 10,
    onImagesSelected: (List<Uri>) -> Unit
): () -> Unit {
    val context = LocalContext.current

    // Multiple Photo Picker 런처
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxItems)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImagesSelected(uris)
        }
    }

    // 권한 요청 런처 (권한 승인 시 자동으로 photo picker 실행)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Android 13+ 에서 "일부 제한된 액세스 허용" 선택 시 isGranted=false이지만
        // photo picker는 실행되어야 하므로 권한 체크 없이 실행
        photoPicker.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    // 권한 체크 및 Photo Picker 실행 함수 반환
    return {
        checkAndRequestPhotoPermission(
            context = context,
            onPermissionGranted = {
                photoPicker.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            onRequestPermission = { permission ->
                permissionLauncher.launch(permission)
            }
        )
    }
}

private fun checkAndRequestPhotoPermission(
    context: Context,
    onPermissionGranted: () -> Unit,
    onRequestPermission: (String) -> Unit
) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            // API 33+ : PickVisualMedia는 시스템 권한 관리 사용, 바로 실행
            // "일부 제한된 액세스 허용" 선택 시에도 선택한 이미지만 접근 가능
            onPermissionGranted()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            // API 30-32 : 시스템 Photo Picker 사용, 권한 불필요
            onPermissionGranted()
        }
        else -> {
            // API 29 이하 : READ_EXTERNAL_STORAGE 권한 필요
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
                // 권한이 있으면 바로 photo picker 실행
                onPermissionGranted()
            } else {
                // 권한 요청
                onRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}