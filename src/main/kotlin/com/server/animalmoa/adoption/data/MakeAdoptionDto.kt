package com.server.animalmoa.adoption.data

import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.common.PostType

data class MakeAdoptionDto(
    val species: String?,
    val breed: String?,
    val region: String? = null,
    val gender: String? = null,
    val title: String? = null,
    val content: String? = null,
    val age: String?,
    val thumbnailUrl: String? = null,
    val postType: PostType,
    val adoptionStatus: AdoptionStatus? = null,
    val originalUrl: String,
    val source: Source,
    val identifier: String?,
    val createdAt: String? = null,
) {
    companion object {
        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT.name,
                breed = CatBreed.SIAMESE.name,
                gender = Gender.MALE.name,
                region = Region.SEOUL.name,
                postType = PostType.FREE_ADOPTION,
                age = "0",
                source = Source.JUSEYO,
                originalUrl = "",
                adoptionStatus = AdoptionStatus.ING,
                identifier = "identi",
            )
    }
}
