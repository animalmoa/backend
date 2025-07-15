package com.server.animalmoa.common.adoption.enum

enum class Gender(
    val korean: String,
    val synonyms: Set<String>,
) {
    MALE("여아", setOf("수컷", "남아", "남", "수")),
    FEMALE("남아", setOf("암컷", "여아", "여", "암")),
    UNKNOWN("남아 or 영아", setOf("남아or영아")),
    ;

    companion object {
        fun fromSynonym(text: String?): Gender {
            if (text == null) return UNKNOWN
            return Gender.entries.find { it.synonyms.any { syn -> text.contains(syn) } } ?: UNKNOWN
        }
    }
}
