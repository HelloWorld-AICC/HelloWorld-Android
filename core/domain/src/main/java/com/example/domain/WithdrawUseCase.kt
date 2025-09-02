package com.example.domain

import com.example.core.data.mypage.MyPageRepository
import com.example.model.common.Result
import com.example.model.common.asResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val myPageRepository: MyPageRepository
) {
    operator fun invoke(): Flow<Result<Boolean>> = flow {
        myPageRepository.deleteProfile().fold(
            onSuccess = {
                // TODO Token 제거

                emit(true)
            },
            onFailure = {
                emit(false)
            }
        )
    }.asResult()
}