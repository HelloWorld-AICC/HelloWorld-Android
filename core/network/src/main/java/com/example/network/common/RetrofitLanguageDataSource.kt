package com.example.network.common

import android.util.Log
import com.example.model.common.LanguageRequest
import retrofit2.HttpException
import javax.inject.Inject

class RetrofitLanguageDataSource @Inject constructor(
    private val languageApi: LanguageApi
) : LanguageDataSource {
    companion object {
        private const val TAG = "RetrofitLanguageDataSource"
    }

    override suspend fun setLanguage(request: LanguageRequest): Result<String> {
        Log.d(TAG, "setLanguage() called")

        return try {
            val response = languageApi.setLanguage(
                id = request.language,
                request = request
            )
            Log.d(TAG, "setLanguage() response received - isSuccessful: ${response.isSuccessful}, code: ${response.code()}")

            val apiResponse = response.body()
            Log.d(TAG, "setLanguage() apiResponse - result: ${apiResponse?.result}")

            when {
                // retrofit error (200번대 이외)
                !response.isSuccessful -> {
                    Log.e(TAG, "setLanguage() HTTP error - code: ${response.code()}, message: ${response.message()}")
                    Result.failure(HttpException(response))
                }

                apiResponse?.isSuccess == true && apiResponse.result != null -> {
                    Log.d(TAG, "setLanguage() success - result: ${apiResponse.result}")
                    Result.success(apiResponse.result)
                }

                // isSuccess = false, result == null
                else -> {
                    Log.e(TAG, "setLanguage() API error - code: ${apiResponse?.code}")
                    Result.failure(Exception("${apiResponse?.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "setLanguage() exception", e)
            Result.failure(e)
        }
    }
}