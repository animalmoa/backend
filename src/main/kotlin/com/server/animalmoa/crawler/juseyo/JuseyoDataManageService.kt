package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.DogBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.crawler.DataManageService
import com.server.animalmoa.exception.IdentifierNotFoundException
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
) : DataManageService {
    val logger = KotlinLogging.logger {}

    override fun parseDataAndSave(makeAdoptionDto: MakeAdoptionDto): Adoption? {
        // 0) Identifier 추출
        val identifier =
            makeAdoptionDto.identifier?.let {
                val parsedIdentifier =
                    urlParser.extractQueryParam(
                        makeAdoptionDto.identifier,
                        "no",
                    ) ?: throw IdentifierNotFoundException()

                adoptionRepositoryService
                    .findAdoption(
                        makeAdoptionDto.source,
                        parsedIdentifier,
                    )?.let {
                        /*
                         만약 해당 Identifier를 가진 Adoption이 이미 저장되어있다면
                         TODO AdoptionStatus 업데이트
                         */
                        return it
                    }
                parsedIdentifier
            } ?: throw IdentifierNotFoundException()

        // 1) 생성 시간 추출
        val createdAt: LocalDateTime? = parseToLocalDateTime(makeAdoptionDto.createdAt)
        logger.info { makeAdoptionDto }

        // 2) 간단한 정보 추출
        val region = Region.fromText(makeAdoptionDto.region)?.name ?: makeAdoptionDto.region
        val ageByMonth = makeAdoptionDto.age
        val gender = Gender.fromText(makeAdoptionDto.gender)?.name
        // 3) species, breed 결정
        val speciesAndBreed = makeAdoptionDto.species?.split("-")
        val speciesText = speciesAndBreed?.getOrNull(0)
        val breedText = speciesAndBreed?.getOrNull(1)
        val species = Species.fromText(speciesText).name
        val breed =
            when (species) {
                Species.DOG.name -> DogBreed.fromText(breedText)?.name
                Species.CAT.name -> CatBreed.fromText(breedText)?.name
                else -> breedText
            } ?: breedText

        return adoptionRepositoryService.save(
            MakeAdoptionDto(
                species = species,
                breed = breed,
                region = region,
                gender = gender,
                age = ageByMonth.toString(),
                thumbnailUrl = makeAdoptionDto.thumbnailUrl,
                originalUrl = makeAdoptionDto.originalUrl,
                source = Source.JUSEYO,
                title = makeAdoptionDto.title,
                content = makeAdoptionDto.content,
                postType = makeAdoptionDto.postType,
                createdAt = createdAt.toString(),
                identifier = identifier,
            ),
        )
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
