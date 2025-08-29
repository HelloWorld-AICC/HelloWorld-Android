package com.example.network.mypage

import android.util.Log
import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.AllSummaryResponse
import com.example.model.mypage.DeleteProfileResponse
import com.example.model.mypage.DetailSummaryRequest
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.PageSizeRequest
import com.example.model.mypage.UpdateProfileResponse
import com.example.model.mypage.UserInfo
import com.example.network.util.compressImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

class RetrofitMyPageDataSource @Inject constructor(
    private val myPageApi: MyPageApi
) : MyPageDataSource {
    companion object {
        private const val TAG = "RetrofitMyPageDataSource"
    }

    override suspend fun getMyPage(): Result<UserInfo> {
        Log.d(TAG, "getMyPage() called")

        return try {
            val response = myPageApi.getMyPage()
            Log.d(TAG, "getMyPage() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getMyPage() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getMyPage() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getMyPage() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getMyPage() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyPage() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun setProfile(nickName: String, userImg: ByteArray?): Result<UpdateProfileResponse> {
        Log.d(TAG, "setProfile() called")

        return try {
            val namePart = nickName.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = userImg?.let { bytes ->
                val compressedBytes = compressImage(bytes, maxSizeKB = 500)

                val requestBody = compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "file",
                    "image.jpg",
                    requestBody
                )
            }

            val response = myPageApi.setProfile(namePart, imagePart)
            Log.d(TAG, "setProfile() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "setProfile() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "setProfile() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "setProfile() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "setProfile() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
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
        Log.d(TAG, "getAllSummary() called")

        return try {
            val response = myPageApi.getAllSummary(request.page, request.size)
            Log.d(TAG, "getAllSummary() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getAllSummary() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getAllSummary() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure<AllSummaryResponse>(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getAllSummary() success - result: ${apiResponse.result}")
                    Result.success<AllSummaryResponse>(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getAllSummary() API error - code: ${apiResponse?.code}")
                    Result.failure<AllSummaryResponse>(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllSummary() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getDetailSummary(request: DetailSummaryRequest): Result<DetailSummaryResponse> {
        Log.d(TAG, "getDetailSummary() called")

        return try {
            val response = myPageApi.getDetailSummary(request.summaryId)
            Log.d(TAG, "getDetailSummary() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getDetailSummary() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getDetailSummary() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure<DetailSummaryResponse>(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getDetailSummary() success - result: ${apiResponse.result}")
                    Result.success<DetailSummaryResponse>(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getDetailSummary() API error - code: ${apiResponse?.code}")
                    Result.failure<DetailSummaryResponse>(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getDetailSummary() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(): Result<DeleteProfileResponse> {
        Log.d(TAG, "deleteProfile() called")

        return try {
            val response = myPageApi.deleteProfile()
            Log.d(TAG, "deleteProfile() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "deleteProfile() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "deleteProfile() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "deleteProfile() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "deleteProfile() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteProfile() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllMyCommunity(request: PageSizeRequest): Result<AllCommunityResponse> {
        Log.d(TAG, "getAllMyCommunity() called")

        return try {
            val response = myPageApi.getAllMyCommunity(request.page, request.size)
            Log.d(TAG, "getAllMyCommunity() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getAllMyCommunity() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getAllMyCommunity() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure<AllCommunityResponse>(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getAllMyCommunity() success - result: ${apiResponse.result}")
                    Result.success<AllCommunityResponse>(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getAllMyCommunity() API error - code: ${apiResponse?.code}")
                    Result.failure<AllCommunityResponse>(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllMyCommunity() exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllMyComment(request: PageSizeRequest): Result<AllCommentResponse> {
        Log.d(TAG, "getAllMyComment() called")

        return try {
            val response = myPageApi.getAllMyComment(request.page, request.size)
            Log.d(TAG, "getAllMyComment() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "getAllMyComment() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "getAllMyComment() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure<AllCommentResponse>(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "getAllMyComment() success - result: ${apiResponse.result}")
                    Result.success<AllCommentResponse>(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "getAllMyComment() API error - code: ${apiResponse?.code}")
                    Result.failure<AllCommentResponse>(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllMyComment() exception", e)
            Result.failure(e)
        }
    }
}