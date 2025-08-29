package com.example.network.mypage

import android.util.Log
import com.example.model.mypage.*
import com.example.network.util.compressImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class RetrofitMyPageDataSource @Inject constructor(
    private val myPageApi: MyPageApi
) : MyPageDataSource {

    companion object {
        private const val TAG = "RetrofitMyPageDataSource"
    }

    override suspend fun getMyPage(): Result<UserInfo> {
        return try {
            val apiResponse = myPageApi.getMyPage()
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Log.d(TAG, "getMyPage() success - result: ${apiResponse.result}")
                Result.success(apiResponse.result)
            } else {
                Log.e(TAG, "getMyPage() API error - code: ${apiResponse.code}")
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyPage() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun setProfile(nickName: String, userImg: ByteArray?): Result<UpdateProfileResponse> {
        return try {
            val namePart = nickName.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = userImg?.let { bytes ->
                val compressedBytes = compressImage(bytes, maxSizeKB = 500)
                MultipartBody.Part.createFormData(
                    "file", "image.jpg",
                    compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
            }

            val apiResponse = myPageApi.setProfile(namePart, imagePart)
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "setProfile() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun setLanguage(language: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSummary(request: PageSizeRequest): Result<AllSummaryResponse> {
        return try {
            val apiResponse = myPageApi.getAllSummary(request.page, request.size)
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllSummary() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getDetailSummary(request: DetailSummaryRequest): Result<DetailSummaryResponse> {
        return try {
            val apiResponse = myPageApi.getDetailSummary(request.summaryId)
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getDetailSummary() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(): Result<DeleteProfileResponse> {
        return try {
            val apiResponse = myPageApi.deleteProfile()
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteProfile() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllMyCommunity(request: PageSizeRequest): Result<AllCommunityResponse> {
        return try {
            val apiResponse = myPageApi.getAllMyCommunity(request.page, request.size)
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllMyCommunity() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllMyComment(request: PageSizeRequest): Result<AllCommentResponse> {
        return try {
            val apiResponse = myPageApi.getAllMyComment(request.page, request.size)
            if (apiResponse.isSuccess && apiResponse.result != null) {
                Result.success(apiResponse.result)
            } else {
                Result.failure(Exception(apiResponse.code))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllMyComment() exception", e)
            Result.failure(e)
        }
    }
}
