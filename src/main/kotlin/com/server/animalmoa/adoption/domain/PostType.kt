package com.server.animalmoa.adoption.domain

enum class PostType(
    val korean: String,
) {
    FREE_ADOPTION("무료 분양"),
    PAID_ADOPTION("유료 분양"),
    REQUIRE_ADOPTION("분양 요청"),
    LOST("실종 동물"),
}
