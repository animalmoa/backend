package com.server.animalmoa.adoption.data

import com.server.animalmoa.adoption.domain.Breed
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species

data class MakeAdoptionDto(
    val species: Species,
    val breed: Breed,
    val gender: Gender,
    val region: Region,
    val title: String,
    val content: String? = null,
    val thumbnailUrl: String,
    val postType: PostType,
    val originalUrl: String?,
    val ageByMonth: Int? = null,
    val source: Source,
) {
    companion object {
        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT,
                breed = CatBreed.SIAM,
                gender = Gender.MALE,
                region = Region.SEOUL,
                thumbnailUrl = "",
                postType = PostType.FREE_ADOPTION,
                originalUrl = "",
                ageByMonth = 0,
                source = Source.JUSEYO,
                title = "title",
                content = "content",
            )
    }
}
