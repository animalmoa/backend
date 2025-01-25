package com.server.animalmoa.adoption.data

import com.server.animalmoa.adoption.domain.AdoptionType
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species

data class MakeAdoptionDto(
    val species: String,
    val breed: String,
    val gender: String,
    val region: String,
    val content: String? = null,
    val thumbnailUrl: String,
    val adoptionType: String,
    val originalUrl: String?,
    val source: String,
) {
    companion object {
        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT.name,
                breed = CatBreed.SIAM.name,
                gender = Gender.MALE.name,
                region = Region.SEOUL.name,
                thumbnailUrl = "",
                adoptionType = AdoptionType.FREE.name,
                originalUrl = "",
                source = Source.JUSEYO.name,
            )
    }
}
