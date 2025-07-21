package com.example.domain

import com.example.core.data.common.LanguageRepository
import com.example.core.data.mypage.MyPageRepository
import com.example.model.common.Language
import com.example.model.common.Result
import com.example.model.common.asResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetProfileUseCase @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val languageRepository: LanguageRepository,
) {
    operator fun invoke(nickName: String, userImg: ByteArray?, language: Language?): Flow<Result<Boolean>> = flow {
        myPageRepository.setProfile(
            nickName = nickName,
            userImg = userImg
        ).onSuccess {
            if (language != null) {
                languageRepository.submitLanguage(language).onSuccess {
                    languageRepository.setLanguage(language)
                }
            }
        }

        emit(true)
    }.asResult()
}