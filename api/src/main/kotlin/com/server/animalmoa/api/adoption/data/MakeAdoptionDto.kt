package com.server.animalmoa.api.adoption.data

import com.server.animalmoa.api.adoption.domain.AdoptionStatus
import com.server.animalmoa.api.adoption.domain.CatBreed
import com.server.animalmoa.api.adoption.domain.Gender
import com.server.animalmoa.api.adoption.domain.Region
import com.server.animalmoa.api.adoption.domain.Source
import com.server.animalmoa.api.adoption.domain.Species
import com.server.animalmoa.api.common.PostType

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
        fun forTest(): com.server.animalmoa.api.adoption.data.MakeAdoptionDto =
            com.server.animalmoa.api.adoption.data.MakeAdoptionDto(
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
