package com.server.animalmoa.crawler.source.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.DogBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.common.PostType
import com.server.animalmoa.crawler.service.DataParser
import com.server.animalmoa.webdriver.UrlParser
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class JuseyoDataManageService(
    private val urlParser: UrlParser,
    private val adoptionRepositoryService: AdoptionRepositoryService,
) : DataParser(urlParser) {
    val logger = KotlinLogging.logger {}

    override fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
        // 0) Identifier 추출
        val identifier = extractIdentifier(rawDto.identifier, "no")

        // 1) 생성 시간 추출
        val createdAt: LocalDateTime? = parseToLocalDateTime(rawDto.createdAt)
        // 2) 간단한 정보 추출
        val region = Region.fromSynonym(rawDto.region).name
        val age = rawDto.age
        val gender = Gender.fromSynonym(rawDto.gender)?.name
        // 3) species, breed 결정
        val speciesAndBreed = rawDto.species?.split("-")
        val speciesText = speciesAndBreed?.getOrNull(0)
        val breedText = speciesAndBreed?.getOrNull(1)
        val species = Species.fromSynonym(speciesText).name
        val breed =
            when (species) {
                Species.DOG.name -> DogBreed.fromSynonym(breedText)?.name
                Species.CAT.name -> CatBreed.fromSynonym(breedText)?.name
                else -> breedText
            } ?: breedText

        // 4) postType에는 img이름이 들어있고 이를 기반으로 파싱
        val postType = parsePostType(rawDto.postType)
        val adoptionStatus = rawDto.adoptionStatus?.let { parseAdoptionStatus(it) }

        val newAdoption =
            Adoption.from(
                MakeAdoptionDto(
                    species = species,
                    breed = breed,
                    region = region,
                    gender = gender,
                    age = age.toString(),
                    thumbnailUrl = rawDto.thumbnailUrl,
                    originalUrl = rawDto.originalUrl,
                    source = Source.JUSEYO,
                    title = rawDto.title,
                    content = rawDto.content,
                    postType = postType.name,
                    adoptionStatus = adoptionStatus?.name,
                    createdAt = createdAt.toString(),
                    identifier = identifier,
                ),
            )

        return adoptionRepositoryService.ifExistUpdateElseSave(newAdoption)
    }

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

    fun parseToLocalDateTime(createdAtText: String?): LocalDateTime? =
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            createdAtText?.let {
                it
                    .trim()
                    .replace("등록일 :", "") // "등록일 :" 제거
                    .trim()
                    .let { dateText -> LocalDateTime.parse(dateText, formatter) }
            }
        } catch (e: DateTimeParseException) {
            logger.error("Error parsing date: ${e.message}")
            null
        }

    /*
    TODO age Parse
    현재 강아지와 고양이마다 age 형식이 다름 강아지(2년 2개월) 고양이(2년 or 2개월)
     */
    private fun parseAgeByMonth(ageText: String?): Int? =
        ageText?.let {
            val trimmed = ageText.trim()
            return when {
                trimmed.endsWith("년") -> {
                    val years = trimmed.removeSuffix("년").trim().toIntOrNull() ?: 0
                    years * 12
                }
                trimmed.endsWith("개월") -> {
                    val months = trimmed.removeSuffix("개월").trim().toIntOrNull() ?: 0
                    months
                }
                else -> null
            }
        }
}
