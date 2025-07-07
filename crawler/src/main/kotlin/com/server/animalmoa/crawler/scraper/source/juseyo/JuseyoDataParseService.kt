package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.domain.AdoptionStatus
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class JuseyoDataParseService {
    val logger = KotlinLogging.logger {}

    fun getMakeAdoptionDto(
        url: String,
        toHtmlFunc: () -> String,
        juseyoDataParser: JuseyoDataParser,
    ): MakeAdoptionDto {
        val html = toHtmlFunc()
        return juseyoDataParser.getMakeAdoptionDto(html, url)
    }

//    override fun processDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
//        // 0) Identifier 추출
//        val identifier = extractIdentifier(rawDto.identifier, "no")
//        // 1) 변환해야하는 데이터들 변환
//        val createdAt: LocalDateTime? = parseToLocalDateTime(rawDto.createdAt)
//
//        val breedText = rawDto.breed
//        val species = rawDto.species
//        val breed =
//            when (species) {
//                Species.DOG.name -> DogBreed.fromSynonym(breedText)?.name
//                Species.CAT.name -> CatBreed.fromSynonym(breedText)?.name
//                else -> breedText
//            } ?: breedText
//
//        // 4) postType에는 img이름이 들어있고 이를 기반으로 파싱
//        val postType = parsePostType(rawDto.postType)
//        val adoptionStatus = rawDto.adoptionStatus?.let { parseAdoptionStatus(it) }
//
//        val newAdoption =
//            Adoption.from(
//                MakeAdoptionDto(
//                    species = species,
//                    breed = breed,
//                    region = Region.fromSynonym(rawDto.region).name,
//                    gender = Gender.fromSynonym(rawDto.gender)?.name,
//                    age = rawDto.age,
//                    thumbnailUrl = rawDto.thumbnailUrl,
//                    originalUrl = rawDto.originalUrl,
//                    source = Source.JUSEYO,
//                    title = rawDto.title,
//                    content = rawDto.content,
//                    postType = postType.name,
//                    adoptionStatus = adoptionStatus?.name,
//                    createdAt = createdAt.toString(),
//                    identifier = identifier,
//                ),
//            )
//        return adoptionRepositoryService.ifExistUpdateElseSaveBySourceAndIdentifier(newAdoption)
//    }

    fun parsePostType(imageSrc: String): PostType {
        if (imageSrc.endsWith("free.gif") || imageSrc.endsWith("ok.gif")) {
            return PostType.FREE_ADOPTION
        }
        if (imageSrc.endsWith("free2.gif")) {
            return PostType.REQUEST_ADOPTION
        }
        return PostType.UNKNOWN
    }

    fun parseAdoptionStatus(imageSrc: String): AdoptionStatus {
        if (imageSrc.endsWith("ok.gif")) {
            return AdoptionStatus.COMPLETED
        }
        return AdoptionStatus.ING
    }
}
