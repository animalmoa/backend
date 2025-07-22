package com.server.animalmoa.common.adoption.enum

enum class Source(
    val korean: String,
    val url: String,
) {
    JUSEYO("주세요닷컴", "https://www.zooseyo.com"),
    ANIMAL_GO("국가동물보호정보시스템", "https://www.animal.go.kr"),
    UMADONG("우마동", "cafe.naver.com/6655happyclub"),
    DOGMARU("도그마루", "dogmaru.co.kr"),
}
