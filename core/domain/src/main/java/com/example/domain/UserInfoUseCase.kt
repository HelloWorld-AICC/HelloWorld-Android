package com.example.domain

import android.util.Log
import com.example.core.data.common.LanguageRepository
import com.example.core.data.mypage.MyPageRepository
import com.example.core.data.token.TokenRepositoryImpl
import com.example.model.common.Result
import com.example.model.common.asResult
import com.example.model.mypage.UserInfo
import com.example.network.interceptor.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// 사용자 정보 가져오는 usecase
class UserInfoUseCase @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val languageRepository: LanguageRepository,
) {
    operator fun invoke(): Flow<Result<UserInfo>> = flow {
        val user = myPageRepository.getMyPage().getOrThrow()
        val language = languageRepository.getLanguage()

        emit(
            UserInfo(
                name = user.name,
                userImg = user.userImg,
                language = language
            )
        )
    }.asResult()
}