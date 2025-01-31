package com.server.animalmoa.adoption.domain

enum class Gender(
    val korean: String,
    val synonyms: Set<String>,
) {
    MALE("수", setOf("수컷", "남아", "남", "수")),
    FEMALE("암", setOf("암컷", "여아", "여", "암")),
    UNKNOWN("미확인", setOf("남아or영아")),
    ;

    companion object {
        fun fromSynonym(text: String?): Gender? =
            text?.let {
                val matched = Gender.entries.find { it.synonyms.any { syn -> text.contains(syn) } }
                return matched ?: UNKNOWN
            }

        fun fromName(type: String?): Gender =
            type?.let {
                Gender.entries.find { it.name.equals(type, ignoreCase = true) } ?: UNKNOWN
            } ?: UNKNOWN
    }
}
