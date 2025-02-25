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
    val region: String?,
    val gender: String?,
    val title: String?,
    val content: String?,
    val age: String?,
    val thumbnailUrl: String?,
    val postType: String,
    val adoptionStatus: String?,
    val originalUrl: String,
    val source: Source,
    val identifier: String?,
    val createdAt: String?,
) {
    companion object {
        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT.name,
                breed = CatBreed.SIAMESE.name,
                gender = Gender.MALE.name,
                region = Region.SEOUL.name,
                postType = PostType.FREE_ADOPTION.name,
                age = "0",
                source = Source.JUSEYO,
                originalUrl = "",
                adoptionStatus = AdoptionStatus.ING.name,
                identifier = "identi",
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                createdAt = "createdAt",
            )
    }
}
