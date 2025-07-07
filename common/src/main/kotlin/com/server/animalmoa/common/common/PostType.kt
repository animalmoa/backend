package com.server.animalmoa.common.common

enum class PostType(
    val korean: String,
) {
    FREE_ADOPTION("무료 분양"),
    PAID_ADOPTION("유료 분양"),
    REQUEST_ADOPTION("분양 요청"),
    LOST("실종 동물"),
    LOST_FOUND("실종 발견"),
    UNKNOWN("미정"),
}
