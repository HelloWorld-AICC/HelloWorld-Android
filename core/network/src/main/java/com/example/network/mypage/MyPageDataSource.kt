package com.example.network.mypage

import android.net.Uri
import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.AllSummaryResponse
import com.example.model.mypage.DeleteProfileResponse
import com.example.model.mypage.DetailSummaryRequest
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.MyLanguageResponse
import com.example.model.mypage.UserInfo
import com.example.model.mypage.PageSizeRequest
import com.example.model.mypage.UpdateProfileResponse

interface MyPageDataSource {
    suspend fun getMyPage(): Result<UserInfo>

    suspend fun getMyLanguage(): Result<MyLanguageResponse>

    suspend fun setProfile(
        nickName: String,
        userImg: ByteArray?,
    ): Result<UpdateProfileResponse>

    suspend fun setLanguage(
        language: Long,
    ): Result<Unit>

    suspend fun getAllSummary(
        request: PageSizeRequest
    ): Result<AllSummaryResponse>

    suspend fun getDetailSummary(
        request: DetailSummaryRequest
    ): Result<DetailSummaryResponse>

    suspend fun deleteProfile(): Result<DeleteProfileResponse>

    suspend fun getAllMyCommunity(
        request: PageSizeRequest
    ): Result<AllCommunityResponse>

    suspend fun getAllMyComment(
        request: PageSizeRequest
    ): Result<AllCommentResponse>
}
