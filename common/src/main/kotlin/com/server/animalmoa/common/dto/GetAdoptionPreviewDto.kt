package com.server.animalmoa.common.dto

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.domain.AdoptionStatus
import com.server.animalmoa.common.adoption.domain.Gender
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.adoption.domain.Species
import java.time.LocalDateTime

// 종류에 따라 색깔 표시가 필요한 정보들에 대하여 String이 아니라 Enum을 넘겨 thymeleaf에서 Enum.color를 사용한다
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
                breed = adoption.breed,
                age = adoption.age,
                viewCount = adoption.viewCount,
                createdAt = adoption.createdAt,
                gender = adoption.gender,
                thumbnailUrl = adoption.thumbnailUrl,
                region = adoption.region.korean,
                originalUrl = adoption.originalUrl,
            )
    }
}
