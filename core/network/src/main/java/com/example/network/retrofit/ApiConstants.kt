package com.example.network.retrofit

object ApiConstants {
    const val BASE_URL = "https://helloworldhelp.shop/mvc/"

    const val MY_PAGE = "myPage/"                           // GET      마이페이지
    const val SET_PROFILE = "myPage/setProfile"             // PATCH    프로필 변경
    const val ALL_SUMMARY = "myPage/allSummary"             // GET      내 전체 채팅 상담 요약 보기
    const val DETAIL_SUMMARY = "myPage/detailSummary"       // GET      채팅 상담 요약 상세 보기
    const val DELETE_PROFILE = "myPage/delete"              // DELETE   회원 탈퇴
    const val ALL_MY_COMMUNITY = "myPage/AllMyCommunity"    // GET      본인 작성 글 조회
    const val ALL_MY_COMMENT = "myPage/AllMyComment"        // GET      본인 작성 댓글 조회

    const val GET_COMMUNITY_POST_LIST = "community/{category_id}/list"                      // GET  글 조회
    const val CREATE_COMMUNITY_POST = "community/{category_id}/create"                      // POST 글 등록
    const val GET_COMMUNITY_POST_DETAIL = "community/detail/{community_id}"   // GET  글 상세 조회
    const val CREATE_COMMUNITY_COMMENT = "community/{community_id}/comment"                 // POST 댓글 등록

    const val GET_LANGUAGE = "myPage/userInfo"                  // GET     언어 조회
    const val SET_LANGUAGE = "myPage/language/{language_id}"    // POST 언어 변경

    const val UPDATE_COMMUNITY_POST = "community/{community_id}/modify"           // PATCH    글 수정
    const val DELETE_COMMUNITY_POST = "community/{category_id}/{community_id}/delete"           // DELETE   글 삭제
    const val DELETE_COMMUNITY_COMMENT = "community/{community_id}/{comment_id}/comment/delete" // DELETE   댓글 등록
    const val REPORT_COMMUNITY_POST = "report/community/{community_id}"                         // POST     글 신고
}