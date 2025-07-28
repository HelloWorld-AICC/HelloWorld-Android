package com.example.network.community

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.model.community.CommentRequest
import com.example.model.community.CommunityRequest
import com.example.model.community.CommunityResponse
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import com.example.model.community.WriteFileRequest
import com.example.model.community.WriteResponse
import com.example.network.util.compressImage
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RetrofitCommunityDataSource @Inject constructor(
    private val communityApi: CommunityApi,
    @ApplicationContext private val context: Context,
) : CommunityDataSource {
    companion object {
        private const val TAG = "RetrofitCommunityDataSource"
    }

    override suspend fun getCommunityPostList(request: CommunityRequest): Result<CommunityResponse> {
        Log.d(TAG, "getCommunityPostList() called")

        return try {
            val response = communityApi.getCommunityPostList(
                categoryId = request.categoryId,
                page = request.page,
                size = request.size
            )
            Log.d(TAG, "getCommunityPostList() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getCommunityPostList() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getCommunityPostList() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getCommunityPostList() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getCommunityPostList() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCommunityPostList() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun submitCommunityPost(category: Long, title: String, content: String, images: List<WriteFileRequest>): Result<WriteResponse> {
        Log.d(TAG, "submitCommunityPost() called")

        return try {
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = images.mapIndexed { index, image ->
                val compressedBytes = compressImage(image.bytes, maxSizeKB = 500)

                val requestBody = compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "images",
                    "image_$index.jpeg",
                    requestBody
                )
            }

            val response = communityApi.submitCommunityPost(
                categoryId = category,
                title = titlePart,
                content = contentPart,
                images = imageParts
            )
            Log.d(TAG, "submitCommunityPost() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "submitCommunityPost() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "submitCommunityPost() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "submitCommunityPost() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "submitCommunityPost() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "submitCommunityPost() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getCommunityPostDetail(request: DetailRequest): Result<DetailResponse> {
        Log.d(TAG, "getCommunityPostDetail() called")

        return try {
            val response = communityApi.getCommunityPostDetail(
                categoryId = request.categoryId,
                communityId = request.communityId,
                page = request.page,
                size = request.size
            )
            Log.d(TAG, "getCommunityPostDetail() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getCommunityPostDetail() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getCommunityPostDetail() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getCommunityPostDetail() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getCommunityPostDetail() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCommunityPostDetail() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun submitComment(communityId: Long, content: String): Result<CommunityResponse> {
        Log.d(TAG, "submitComment() called")

        return try {
            val response = communityApi.submitComment(
                communityId = communityId,
                request = CommentRequest(content)
            )
            Log.d(TAG, "submitComment() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "submitComment() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "submitComment() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "submitComment() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "submitComment() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "submitComment() exception", e)
            Result.failure(e)
        }
    }
}