package com.server.animalmoa.adoption.domain

enum class CatBreed(
    override val korean: String,
    val synonyms: Set<String>,
) : Breed {
    SIAM("샴", setOf("샴고양이", "샴")),
    PERSIAN("페르시안", setOf("페르시안고양이", "페르시안")),
    MAINE_COON("메인쿤", setOf("메인 쿤", "메인쿤")),
    ;

    companion object {
        fun fromText(text: String): CatBreed? {
            val matched = entries.find { it.synonyms.any { syn -> text.contains(syn) } }
            return matched
        }
    }
}
