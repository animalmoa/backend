package com.server.animalmoa.common.adoption.enum

enum class Source(
    val korean: String,
    val url: String,
) {
    // 분양 완료, 분양 중 글들이 섞여있는 방식
    JUSEYO("주세요닷컴", "https://www.zooseyo.com"),

    // 분양 완료, 분양 중 글들이 섞여있는 방식
    ANIMAL_GO("국가동물보호정보시스템", "https://www.animal.go.kr"),

    // 분양 완료, 분양 중 글들이 섞여있는 방식
    // 사이트 자체적으로 옛날 글의 작성일을 최신화 한다.
    WURIPET("우리펫", "https://wooripet.co.kr"),

    // 분양 중인 글들만 있는 방식
    KARA("동물권행동-카라", "https://www.ekara.org"),

    // 아래는 보류중
    UMADONG("우마동", "cafe.naver.com/6655happyclub"),
    DOGMARU("도그마루", "https://dogmaru.co.kr"),
}
