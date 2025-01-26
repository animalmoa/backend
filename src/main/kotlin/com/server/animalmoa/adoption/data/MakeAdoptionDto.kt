package com.server.animalmoa.adoption.data

import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species

data class MakeAdoptionDto(
    val species: String,
    val breed: String,
    val region: String,
    val gender: Gender,
    val title: String? = null,
    val content: String? = null,
    val ageByMonth: Int? = null,
    val thumbnailUrl: String? = null,
    val postType: PostType,
    val originalUrl: String? = null,
    val source: Source,
) {
    companion object {
        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT.name,
                breed = CatBreed.SIAM.name,
                gender = Gender.MALE,
                region = Region.SEOUL.name,
                postType = PostType.FREE_ADOPTION,
                ageByMonth = 0,
                source = Source.JUSEYO,
            )
    }
}
