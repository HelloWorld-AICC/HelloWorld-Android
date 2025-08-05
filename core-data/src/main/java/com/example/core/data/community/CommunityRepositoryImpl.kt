package com.example.core.data.community

import android.net.Uri
import android.util.Log
import com.example.model.community.CommunityRequest
import com.example.model.community.CommunityResponse
import com.example.model.community.DetailRequest
import com.example.model.community.DetailResponse
import com.example.model.community.WriteFileRequest
import com.example.model.community.WriteResponse
import com.example.network.community.CommunityDataSource
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val communityDataSource: CommunityDataSource
) : CommunityRepository {

    override suspend fun getCommunityPostList(request: CommunityRequest): Result<CommunityResponse> {
        return communityDataSource.getCommunityPostList(request)
    }

    override suspend fun submitCommunityPost(category: Long, title: String, content: String, images: List<WriteFileRequest>): Result<WriteResponse> {
        return communityDataSource.submitCommunityPost(category, title, content, images)
    }

    override suspend fun getCommunityPostDetail(request: DetailRequest): Result<DetailResponse> {
        return communityDataSource.getCommunityPostDetail(request)
    }

    override suspend fun submitComment(communityId: Long, content: String): Result<CommunityResponse> {
        return communityDataSource.submitComment(communityId, content)
    }
}