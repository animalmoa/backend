package com.server.animalmoa.common.adoption.enum

enum class Species(
    val korean: String,
    val synonyms: Set<String>,
    val color: String,
) {
    DOG("강아지", setOf("강아지", "개"), "#90B6FF"),
    CAT("고양이", setOf("고양이", "냥이"), "#B3A5FF"),
    UNKNOWN("미정", setOf(), "gray"),
    ;

    companion object {
        fun fromSynonym(input: String?): Species =
            if (input == null) {
                UNKNOWN
            } else {
                Species.entries.find { it.synonyms.contains(input) } ?: UNKNOWN
            }

        fun getExceptUnknown(): Array<Species> = entries.filterNot { it == UNKNOWN }.toTypedArray()
    }
}
