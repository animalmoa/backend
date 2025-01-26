package com.server.animalmoa.adoption.domain

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.common.BaseTime

data class Adoption(
    var id: Long? = null,
    var title: String = "",
    var content: String = "",
    val thumbnailUrl: String = "",
    var adoptionType: String = "",
    var originalUrl: String = "",
    var source: String = "",
    var viewCount: Int = 0,
    var species: String = "",
    var breed: String = "",
    var gender: String = "",
    var region: String = "",
) : BaseTime() {
    companion object {
        fun from(makeAdoptionDto: MakeAdoptionDto): Adoption =
            Adoption(
                species = makeAdoptionDto.species.korean,
                breed = makeAdoptionDto.breed.korean,
                gender = makeAdoptionDto.gender.korean,
                region = makeAdoptionDto.region.korean,
                title = makeAdoptionDto.title,
                content = makeAdoptionDto.content ?: "",
                thumbnailUrl = makeAdoptionDto.thumbnailUrl,
                adoptionType = makeAdoptionDto.postType.korean,
                originalUrl = makeAdoptionDto.originalUrl ?: "",
                source = makeAdoptionDto.source.korean,
                viewCount = 0,
            )
    }
}
