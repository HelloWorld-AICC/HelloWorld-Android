package com.example.network.retrofit

object ApiConstants {
    const val BASE_URL = "https://www.gotoend.store/mvc/"

    const val MY_PAGE = "myPage/"                           // GET      마이페이지
    const val SET_PROFILE = "myPage/setProfile"             // PATCH    프로필 변경
    const val ALL_SUMMARY = "myPage/allSummary"             // GET      내 전체 채팅 상담 요약 보기
    const val DETAIL_SUMMARY = "myPage/detailSummary"       // GET      채팅 상담 요약 상세 보기
    const val DELETE_PROFILE = "myPage/delete"              // DELETE   회원 탈퇴
    const val ALL_MY_COMMUNITY = "myPage/AllMyCommunity"    // GET      본인 작성 글 조회
    const val ALL_MY_COMMENT = "myPage/AllMyComment"        // GET      본인 작성 댓글 조회

    const val GET_COMMUNITY_POST_LIST = "community/{category_id}/list"                      // GET
    const val CREATE_COMMUNITY_POST = "community/{category_id}/create"                      // POST
    const val GET_COMMUNITY_POST_DETAIL = "community/{category_id}/detail/{community_id}"   // GET
    const val CREATE_COMMUNITY_COMMENT = "community/{community_id}/comment"                 // POST

    const val SET_LANGUAGE = "myPage/language/{language_id}"    // POST

    const val DELETE_COMMUNITY_POST = "community/{category_id}/{community_id}/delete"
    const val DELETE_COMMUNITY_COMMENT = "community/{community_id}/{comment_id}/delete"
}