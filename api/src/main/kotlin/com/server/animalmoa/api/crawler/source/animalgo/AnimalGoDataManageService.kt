package com.server.animalmoa.api.crawler.source.animalgo

import com.server.animalmoa.api.adoption.data.MakeAdoptionDto
import com.server.animalmoa.api.adoption.domain.Adoption
import com.server.animalmoa.api.adoption.domain.Breed
import com.server.animalmoa.api.adoption.domain.Gender
import com.server.animalmoa.api.adoption.domain.Region
import com.server.animalmoa.api.adoption.domain.Source
import com.server.animalmoa.api.adoption.domain.Species
import com.server.animalmoa.api.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.api.crawler.service.DataManager
import com.server.animalmoa.api.webdriver.UrlParser
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class AnimalGoDataManageService(
    private val urlParser: UrlParser,
    private val adoptionRepositoryService: AdoptionRepositoryService,
) : DataManager(urlParser) {
    private val logger = KotlinLogging.logger {}

    override fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
        logger.info {
            rawDto
        }
        // 0) Identifier 추출
        val identifier = extractIdentifier(rawDto.identifier, "desertionNo")
        // 1) 데이터 변환
        val createdAt: LocalDateTime? = parseToLocalDateTime(rawDto.createdAt)
        val region = Region.fromSynonym(rawDto.region?.split("-")?.getOrNull(0)).name

        val speciesAndBreed = rawDto.species?.split("]")
        val speciesText = speciesAndBreed?.getOrNull(0)?.substringAfter("[")
        val breedText = speciesAndBreed?.getOrNull(1)
        val species = Species.fromSynonym(speciesText).name
        val breed =
            Breed.toEnumWithSynonym(
                species,
                breedText,
            )

        val newAdoption =
            Adoption.from(
                MakeAdoptionDto(
                    species = species,
                    breed = breed,
                    region = region,
                    gender = Gender.fromSynonym(rawDto.gender)?.name,
                    age = rawDto.age,
                    thumbnailUrl = rawDto.thumbnailUrl,
                    originalUrl = rawDto.originalUrl,
                    source = Source.ANIMAL_GO,
                    title = rawDto.title,
                    content = rawDto.content,
                    postType = rawDto.postType,
                    adoptionStatus = rawDto.adoptionStatus,
                    createdAt = createdAt.toString(),
                    identifier = identifier,
                ),
            )

        // 5) 이미 Identifier로 존재하고 있다면 업데이트, 아니라면 save
        return adoptionRepositoryService.ifExistUpdateElseSaveBySourceAndIdentifier(newAdoption)
    }

    /**
     * 예: rawDto.createdAt = "2025-01-25"
     * 단순히 "yyyy-MM-dd" 날짜 형태라면 atStartOfDay()로 LocalDateTime 만들 수 있음.
     * 실제 사이트에서 DateTimeFormatter 패턴 맞춰 조정 필요.
     * TODO LocalDataTime으로 파싱하는 공통 메소드
     */
    private fun parseToLocalDateTime(createdAtText: String?): LocalDateTime? {
        if (createdAtText.isNullOrBlank()) return null
        return try {
            // 단순 yyyy-MM-dd 로 들어온다고 가정
            LocalDate
                .parse(createdAtText.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay()
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
/*
MakeAdoptionDto의 형식
MakeAdoptionDto(
species=[고양이]한국 고양이,
breed=[고양이]한국 고양이,
region=광주-북구-2025-00020,
gender=수컷,
title=[고양이]한국 고양이,
content=본 페이지는 공고기간이 지난 '보호중 동물'의 목록입니다.
실종 동물은 실종 동물공고 페이지에서 확인 하실 수 있습니다.,
age=2024(년생) / 2.4 (Kg),
thumbnailUrl=1666.jpeg,
postType=FREE_ADOPTION,
adoptionStatus=ING,
originalUrl==&desertionNo=429362202500020&menuNo=,
identifier=&desertionNo=429362202500020&menuNo=
createdAt=2025-01-25 )
 */
