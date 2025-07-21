package com.example.network.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun compressImage(imageBytes: ByteArray, maxSizeKB: Int = 500): ByteArray {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return imageBytes

        val rotatedBitmap = correctImageOrientation(bitmap, imageBytes)

        val resizedBitmap = resizeBitmap(rotatedBitmap, 1024, 1024)

        var quality = 90
        var compressedBytes: ByteArray

        do {
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            compressedBytes = outputStream.toByteArray()
            quality -= 10
        } while (compressedBytes.size > maxSizeKB * 1024 && quality > 10)

        if (bitmap != rotatedBitmap) bitmap.recycle()
        if (rotatedBitmap != resizedBitmap) rotatedBitmap.recycle()
        resizedBitmap.recycle()

        compressedBytes
    } catch (e: Exception) {
        imageBytes
    }
}

fun correctImageOrientation(bitmap: Bitmap, imageBytes: ByteArray): Bitmap {
    return try {
        val inputStream = ByteArrayInputStream(imageBytes)
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            else -> return bitmap // 회전이 필요없는 경우
        }

        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        bitmap // 오류 발생시 원본 반환
    }
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val scaleWidth = maxWidth.toFloat() / width
    val scaleHeight = maxHeight.toFloat() / height
    val scale = minOf(scaleWidth, scaleHeight)

    if (scale >= 1.0f) return bitmap

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
}