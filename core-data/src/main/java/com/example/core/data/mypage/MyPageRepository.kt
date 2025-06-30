package com.example.core.data.mypage

import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.AllSummaryResponse
import com.example.model.mypage.DeleteProfileResponse
import com.example.model.mypage.DetailSummaryRequest
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.MyPageResponse
import com.example.model.mypage.PageSizeRequest
import com.example.model.mypage.UpdateProfileResponse
import java.io.File

interface MyPageRepository {
    suspend fun getMyPage(): Result<MyPageResponse>

    suspend fun setProfile(
        nickName: String,
        file: File?
    ): Result<UpdateProfileResponse>

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