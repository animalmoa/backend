package com.server.animalmoa.api.adoption.domain

enum class Species(
    val korean: String,
    val synonyms: Set<String>,
    val color: String,
) {
    DOG("강아지", setOf("강아지", "개"), "royalblue"),
    CAT("고양이", setOf("고양이", "냥이"), "blueviolet"),
    UNKNOWN("미정", setOf(), "gray"),
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

        fun getExceptUnknown(): Array<Species> = entries.filterNot { it == UNKNOWN }.toTypedArray()
    }
}
