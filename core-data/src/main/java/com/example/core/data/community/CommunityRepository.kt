package com.example.core.data.community

import android.net.Uri
import com.example.model.community.CommunityRequest
import com.example.model.community.CommunityResponse
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import com.example.model.community.WriteFileRequest
import com.example.model.community.WriteResponse

interface CommunityRepository {
    suspend fun getCommunityPostList(request: CommunityRequest): Result<CommunityResponse>
    suspend fun submitCommunityPost(category: Long, title: String, content: String, images: List<WriteFileRequest>): Result<WriteResponse>   // return CommunityId
    suspend fun getCommunityPostDetail(request: DetailRequest): Result<DetailResponse>
    suspend fun submitComment(communityId: Long, content: String): Result<CommunityResponse>   // return CommentId
}