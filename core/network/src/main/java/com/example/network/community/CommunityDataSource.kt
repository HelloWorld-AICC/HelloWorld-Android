package com.example.network.community

import android.net.Uri
import com.example.model.community.CommunityRequest
import com.example.model.community.CommunityResponse
import com.example.model.community.DeleteCommentResponse
import com.example.model.community.DeletePostResponse
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import com.example.model.community.WriteFileRequest
import com.example.model.community.WriteResponse

interface CommunityDataSource {
    suspend fun getCommunityPostList(request: CommunityRequest): Result<CommunityResponse>
    suspend fun submitCommunityPost(category: Long, title: String, content:String, images: List<WriteFileRequest>): Result<WriteResponse>
    suspend fun getCommunityPostDetail(request: DetailRequest): Result<DetailResponse>
    suspend fun submitComment(communityId: Long, content: String): Result<CommunityResponse>

    //    suspend fun updatePost()
    suspend fun deletePost(categoryId: Long, communityId: Long): Result<DeletePostResponse>
    suspend fun deleteComment(communityId: Long, commentId: Long): Result<DeleteCommentResponse>
//    suspend fun reportPost()
//    suspend fun reportComment()
}