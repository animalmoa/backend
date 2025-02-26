package com.server.animalmoa.common.adoption.domain

enum class CatBreed(
    override val korean: String,
    val synonyms: Set<String>,
) : Breed {
    NORWEGIAN_FOREST("노르웨이 숲", setOf("노르웨이 숲")),
    DEVON_REX("데본렉스", setOf("데본렉스")),
    RUSSIAN_BLUE("러시안 블루", setOf("러시안 블루")),
    RAGDOLL("렉돌", setOf("렉돌")),
    MUNCHKIN("먼치킨", setOf("먼치킨")),
    MAINE_COON("메인쿤", setOf("메인쿤")),
    BENGAL("뱅갈", setOf("뱅갈")),
    BRITISH_SHORTHAIR("브리티쉬 숏헤어", setOf("브리티쉬 숏헤어")),
    SIAMESE("샴", setOf("샴")),
    SCOTTISH_FOLD("스코티쉬 폴드", setOf("스코티쉬 폴드")),
    SPHYNX("스핑크스", setOf("스핑크스")),
    SINGAPURA("싱가푸라", setOf("싱가푸라")),
    AMERICAN_SHORTHAIR("아메리칸 숏헤어", setOf("아메리칸 숏헤어")),
    ABYSSINIAN("아비시니안", setOf("아비시니안")),
    EXOTIC_SHORTHAIR("엑조틱 숏헤어", setOf("엑조틱 숏헤어")),
    ORIENTAL_LONGHAIR("오리엔탈 롱헤어", setOf("오리엔탈 롱헤어")),
    ORIENTAL_SHORTHAIR("오리엔탈 숏헤어", setOf("오리엔탈 숏헤어")),
    CHINCHILLA("친칠라", setOf("친칠라")),
    TURKISH_ANGORA("터키쉬 앙고라", setOf("터키쉬 앙고라")),
    PERSIAN("페르시안", setOf("페르시안")),
    KOREAN_CAT("한국 고양이", setOf("한국 고양이", "코리안 숏헤어")),
    ;

    companion object {
        fun fromSynonym(text: String?): CatBreed? =
            text?.let {
                val matched = entries.find { it.synonyms.any { syn -> text.contains(syn) } }
                return matched
            }

        fun toKorean(text: String): String =
            CatBreed.entries
                .find { breed ->
                    breed.name.equals(text, ignoreCase = true)
                }?.korean ?: text
    }
}
