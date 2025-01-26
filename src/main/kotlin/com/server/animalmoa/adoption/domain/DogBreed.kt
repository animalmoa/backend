package com.server.animalmoa.adoption.domain

enum class DogBreed(
    override val korean: String,
    val synonyms: Set<String>,
) : Breed {
    BULLDOG("불독", setOf("불독")),
    ;

    companion object {
        fun fromText(text: String): CatBreed? {
            val matched = CatBreed.entries.find { it.synonyms.any { syn -> text.contains(syn) } }
            return matched
        }
    }
}
