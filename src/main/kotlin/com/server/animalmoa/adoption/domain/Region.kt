package com.server.animalmoa.adoption.domain

enum class Region(
    val korean: String,
    val synonyms: Set<String>,
) {
    WIDE("전국", setOf("전국", "전국전체", "전국분양")),
    SEOUL("서울", setOf("서울", "서울시")),
    INCHEON("인천", setOf("인천", "인천시")),
    DAEGU("대구", setOf("대구", "대구시")),
    DAEJEON("대전", setOf("대전", "대전시")),
    GWANGJU("광주", setOf("광주", "광주시")),
    ULSAN("울산", setOf("울산", "울산시")),
    BUSAN("부산", setOf("부산", "부산시")),
    GANGWON("강원", setOf("강원", "강원도")),
    CHUNGBUK("충북", setOf("충북", "충청북도")),
    CHUNGNAM("충남", setOf("충남", "충청남도")),
    JEONBUK("전북", setOf("전북", "전라북도")),
    JEONNAM("전남", setOf("전남", "전라남도")),
    GYEONGBUK("경북", setOf("경북", "경상북도")),
    GYEONGNAM("경남", setOf("경남", "경상남도")),
    GYEONGGI("경기", setOf("경기", "경기도")),
    JEJU("제주", setOf("제주", "제주도")),
    ;

    companion object {
        fun fromText(input: String?): Region? =
            input?.let {
                val text = input.trim()
                // 1) 각 Region의 synonyms 중 하나라도 포함하면 매칭
                val matched =
                    values().find { region ->
                        // synonyms.any { normalized.contains(it) } 도 가능
                        region.synonyms.any { synonym -> text.contains(synonym) }
                    }
                return matched
            }
    }
}
