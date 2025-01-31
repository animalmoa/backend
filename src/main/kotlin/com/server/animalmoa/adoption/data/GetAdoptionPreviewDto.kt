package com.server.animalmoa.adoption.data

import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.Breed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import java.time.LocalDateTime

data class GetAdoptionPreviewDto(
    val id: Long,
    val species: Species,
    var source: Source,
    var gender: Gender,
    var adoptionStatus: AdoptionStatus,
    var region: String,
    val title: String,
    val breed: String,
    val age: String,
    var thumbnailUrl: String,
    val originalUrl: String,
    val viewCount: Int,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(adoption: Adoption): GetAdoptionPreviewDto =
            GetAdoptionPreviewDto(
                id = adoption.id!!,
                species = adoption.species,
                source = adoption.source,
                adoptionStatus = adoption.adoptionStatus,
                title = adoption.title,
                breed = Breed.toKorean(adoption.species, adoption.breed),
                age = adoption.age,
                viewCount = adoption.viewCount,
                createdAt = adoption.createdAt,
                gender = adoption.gender,
                thumbnailUrl = adoption.thumbnailUrl,
                region = Region.toKorean(adoption.region),
                originalUrl = adoption.originalUrl,
            )
    }
}
