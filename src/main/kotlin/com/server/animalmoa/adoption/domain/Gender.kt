package com.server.animalmoa.adoption.domain

import java.awt.SystemColor.text

enum class Gender(
    val korean: String,
    val synonyms: Set<String>,
) {
    MALE("수", setOf("수컷", "남아", "남")),
    FEMALE("암", setOf("암컷", "여아", "암")),
    NOT_DECIDED("미확인", setOf("남아or영아")),
    ;

    companion object {
        fun fromText(text: String?): Gender? =
            text?.let {
                val matched = Gender.entries.find { it.synonyms.any { syn -> text.contains(syn) } }
                return matched ?: NOT_DECIDED
            }
    }
}
