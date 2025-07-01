package com.example.core.data.token

import com.example.core.data.network.RetrofitInstance
import com.example.network.interceptor.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(

) : TokenRepository {
    override suspend fun getAccessToken(): String {
        return RetrofitInstance.getAccessToken()
    }
}