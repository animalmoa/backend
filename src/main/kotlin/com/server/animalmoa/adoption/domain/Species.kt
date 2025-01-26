package com.server.animalmoa.adoption.domain

enum class Species(
    val korean: String,
    val synonyms: Set<String>,
) {
    DOG("개", setOf("강아지", "개")),
    CAT("고양이", setOf("고양이", "냥이")),
    ;

    companion object {
        fun fromText(input: String): Species? {
            val normalized = input.trim()
            val matched =
                entries.find { species ->
                    species.synonyms.any { normalized.contains(it) }
                }
            return matched
        }
    }
}
