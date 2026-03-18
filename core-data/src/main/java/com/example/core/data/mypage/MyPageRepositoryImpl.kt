package com.example.core.data.mypage

import android.net.Uri
import com.example.model.common.Language
import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.AllSummaryResponse
import com.example.model.mypage.DeleteProfileResponse
import com.example.model.mypage.DetailSummaryRequest
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.MyLanguageResponse
import com.example.model.mypage.PageSizeRequest
import com.example.model.mypage.UpdateProfileResponse
import com.example.model.mypage.UserInfo
import com.example.network.mypage.MyPageDataSource
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageDataSource: MyPageDataSource
) : MyPageRepository {

    override suspend fun getMyPage(): Result<UserInfo> {
        return myPageDataSource.getMyPage()
    }

    override suspend fun getMyLanguage(): Result<MyLanguageResponse> {
        return myPageDataSource.getMyLanguage()
    }

    override suspend fun setProfile(nickName: String, userImg: ByteArray?): Result<UpdateProfileResponse> {
        return myPageDataSource.setProfile(nickName, userImg)
    }

    override suspend fun getAllSummary(request: PageSizeRequest): Result<AllSummaryResponse> {
        return myPageDataSource.getAllSummary(request)
    }

    override suspend fun getDetailSummary(request: DetailSummaryRequest): Result<DetailSummaryResponse> {
        return myPageDataSource.getDetailSummary(request)
    }

    override suspend fun deleteProfile(): Result<DeleteProfileResponse> {
        return myPageDataSource.deleteProfile()
    }

    override suspend fun getAllMyCommunity(request: PageSizeRequest): Result<AllCommunityResponse> {
        return myPageDataSource.getAllMyCommunity(request)
    }

    override suspend fun getAllMyComment(request: PageSizeRequest): Result<AllCommentResponse> {
        return myPageDataSource.getAllMyComment(request)
    }
}
