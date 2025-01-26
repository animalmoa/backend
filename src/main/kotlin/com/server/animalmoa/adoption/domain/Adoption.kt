package com.server.animalmoa.adoption.domain

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.common.BaseTime

// Jpa로 바꾼다면... 기본값을 설정해주지 않아도 된다.
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
                species = makeAdoptionDto.species,
                breed = makeAdoptionDto.breed,
                gender = makeAdoptionDto.gender.name,
                region = makeAdoptionDto.region,
                title = makeAdoptionDto.title ?: "",
                content = makeAdoptionDto.content ?: "",
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: "",
                adoptionType = makeAdoptionDto.postType.name,
                originalUrl = makeAdoptionDto.originalUrl ?: "",
                source = makeAdoptionDto.source.name,
                viewCount = 0,
            )
    }
}
