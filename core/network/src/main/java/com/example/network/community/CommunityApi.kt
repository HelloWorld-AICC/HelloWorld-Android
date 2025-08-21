package com.example.network.community

import com.example.model.community.CommentRequest
import com.example.model.community.CommunityResponse
import com.example.model.community.DeleteCommentResponse
import com.example.model.community.DeletePostResponse
import com.example.model.community.DetailResponse
import com.example.model.community.WriteResponse
import com.example.network.response.ApiResponse
import com.example.network.retrofit.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityApi {
    @GET(ApiConstants.GET_COMMUNITY_POST_LIST)
    suspend fun getCommunityPostList(
        @Path("category_id") categoryId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<CommunityResponse>>

    @Multipart
    @POST(ApiConstants.CREATE_COMMUNITY_POST)
    suspend fun submitCommunityPost(
        @Path("category_id") categoryId: Long,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>?,
    ): Response<ApiResponse<WriteResponse>>

    @GET(ApiConstants.GET_COMMUNITY_POST_DETAIL)
    suspend fun getCommunityPostDetail(
        @Path("category_id") categoryId: Long,
        @Path("community_id") communityId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<DetailResponse>>

    @POST(ApiConstants.CREATE_COMMUNITY_COMMENT)
    suspend fun submitComment(
        @Path("community_id") communityId: Long,
        @Body request: CommentRequest
    ): Response<ApiResponse<CommunityResponse>>

    @DELETE(ApiConstants.DELETE_COMMUNITY_POST)
    suspend fun deletePost(
        @Path("category_id") categoryId: Long,
        @Path("community_id") communityId: Long,
    ): Response<ApiResponse<DeletePostResponse>>

    @DELETE(ApiConstants.DELETE_COMMUNITY_COMMENT)
    suspend fun deleteComment(
        @Path("community_id") communityId: Long,
        @Path("comment_id") commentId: Long,
    ): Response<ApiResponse<DeleteCommentResponse>>
}