package com.server.animalmoa.common.dto

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Breed
import com.server.animalmoa.common.adoption.enum.Gender
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.common.PostType
import mu.KotlinLogging

// 모든 사이트마다 공통으로 쓰일 수 있도록 최대한 많은 필드를 String을 인자로 받는다.
data class MakeAdoptionDto(
    val species: String?,
    val breed: String?,
    val region: String?,
    val gender: String?,
    val title: String?,
    val content: String?,
    val age: String?,
    val thumbnailUrl: String?,
    val createdAt: String?,
    // nullable 한 것들에 대하여 별도의 에러처리를 하지 않는다.
    val originalUrl: String,
    val adoptionStatus: AdoptionStatus,
    val source: Source,
    val identifier: String,
    val postType: PostType,
) {
    init {
        logger.info { "$this" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun forTest(): MakeAdoptionDto =
            MakeAdoptionDto(
                species = Species.CAT.name,
                breed = Breed.SIAMESE.korean,
                gender = Gender.MALE.name,
                region = Region.SEOUL.name,
                postType = PostType.FREE_ADOPTION,
                age = "0",
                source = Source.JUSEYO,
                originalUrl = "url",
                adoptionStatus = AdoptionStatus.ING,
                identifier = "identi",
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                createdAt = "createdAt",
            )
    }
}
