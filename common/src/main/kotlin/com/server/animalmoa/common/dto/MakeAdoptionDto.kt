package com.server.animalmoa.common.dto

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Breed
import com.server.animalmoa.common.adoption.enum.Gender
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.common.PostType
import mu.KotlinLogging
import java.time.LocalDateTime

data class MakeAdoptionDto(
    // species와 같이 사이트마다 같을 수 있는 "한국 고양이"에서 값을 추출해낼 수 있는 것은 String으로 받아서 Adoption에서 공통처리
    // AdoptionStatus와 같이 사이트마다 추출하는 방법이 다른 것은 Enum
    val species: String?,
    val breed: String?,
    val region: String?,
    val gender: String?,
    val title: String?,
    val content: String?,
    val age: String?,
    val thumbnailUrl: String?,
    val createdAt: LocalDateTime?,
    // nullable 한 것들에 대하여 별도의 에러처리를 하지 않는다.
    val originalUrl: String,
    val adoptionStatus: AdoptionStatus,
    val source: Source,
    val postType: PostType,
    val identifier: String,
) {
    private val logger = KotlinLogging.logger {}

    init {
        // MakeAdoptionDto의 궁극적인 목적은, Raw한 데이터를 Adoption에 맞는 값으로 변형하기전에 값을 보기 위함이다.
        logger.info { "MakeAdoptionDto: $this" }
    }

    companion object {
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
                createdAt = LocalDateTime.now(),
            )
    }
}
