package com.server.animalmoa.common.adoption.domain

interface Breed {
    val korean: String

    companion object {
        fun toKorean(
            species: Species,
            breedText: String,
        ): String =
            when (species) {
                Species.DOG -> DogBreed.toKorean(breedText)
                Species.CAT -> CatBreed.toKorean(breedText)
                else -> breedText
            }

        fun find(
            speciesText: String?,
            breedText: String?,
        ): String? {
            if (speciesText.isNullOrBlank() || breedText.isNullOrBlank()) return null
            return when (speciesText) {
                Species.DOG.name -> DogBreed.fromSynonym(breedText)?.name
                Species.CAT.name -> CatBreed.fromSynonym(breedText)?.name
                else -> breedText
            }
        }
    }
}
