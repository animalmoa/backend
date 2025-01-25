package com.server.animalmoa.adoption.domain

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.common.BaseTime

data class Adoption(
    var id: Long? = null,
    var content: String = "",
    val thumbnailUrl: String = "",
    var adoptionType: String = "",
    var originalUrl: String? = "",
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
                species = makeAdoptionDto.species,
                breed = makeAdoptionDto.breed,
                gender = makeAdoptionDto.gender,
                region = makeAdoptionDto.region,
                content = makeAdoptionDto.content ?: "",
                thumbnailUrl = makeAdoptionDto.thumbnailUrl,
                adoptionType = makeAdoptionDto.adoptionType,
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                viewCount = 0,
            )
    }
}
