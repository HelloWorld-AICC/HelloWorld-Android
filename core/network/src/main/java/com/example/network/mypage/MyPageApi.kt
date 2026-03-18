package com.example.network.mypage

import com.example.model.mypage.AllCommentResponse
import com.example.model.mypage.AllCommunityResponse
import com.example.model.mypage.AllSummaryResponse
import com.example.model.mypage.DeleteProfileResponse
import com.example.model.mypage.DetailSummaryResponse
import com.example.model.mypage.MyLanguageResponse
import com.example.model.mypage.UpdateProfileResponse
import com.example.model.mypage.UserInfo
import com.example.network.response.ApiResponse
import com.example.network.retrofit.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MyPageApi {
    @GET(ApiConstants.MY_PAGE)
    suspend fun getMyPage(): Response<ApiResponse<UserInfo>>

    @GET(ApiConstants.GET_LANGUAGE)
    suspend fun getMyLanguage(): Response<ApiResponse<MyLanguageResponse>>

    @POST(ApiConstants.SET_LANGUAGE)
    suspend fun setLanguage(
        @Path("language_id") languageId: Long
    ): Response<ApiResponse<String>>

    @Multipart
    @PATCH(ApiConstants.SET_PROFILE)
    suspend fun setProfile(
        @Part("nickName") nickName: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<ApiResponse<UpdateProfileResponse>>

    @GET(ApiConstants.ALL_SUMMARY)
    suspend fun getAllSummary(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<AllSummaryResponse>>

    @GET(ApiConstants.DETAIL_SUMMARY)
    suspend fun getDetailSummary(
        @Query("summary-id") summaryId: Long
    ): Response<ApiResponse<DetailSummaryResponse>>

    @DELETE(ApiConstants.DELETE_PROFILE)
    suspend fun deleteProfile(): Response<ApiResponse<DeleteProfileResponse>>

    @GET(ApiConstants.ALL_MY_COMMUNITY)
    suspend fun getAllMyCommunity(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<AllCommunityResponse>>

    @GET(ApiConstants.ALL_MY_COMMENT)
    suspend fun getAllMyComment(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<AllCommentResponse>>
}
