package com.server.animalmoa.adoption.domain

enum class Species(
    val korean: String,
    val synonyms: Set<String>,
) {
    DOG("강아지", setOf("강아지", "개")),
    CAT("고양이", setOf("고양이", "냥이")),
    UNKNOWN("미정", setOf()),
    ;

    companion object {
        fun fromSynonym(input: String?): Species =
            input?.let {
                val normalized = input.trim()
                val matched =
                    entries.find { species ->
                        species.synonyms.any { normalized.contains(it) }
                    }
                return matched ?: UNKNOWN
            } ?: UNKNOWN

        fun fromName(name: String?): Species =
            name?.let {
                Species.entries.find { it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
            } ?: UNKNOWN
    }
}
