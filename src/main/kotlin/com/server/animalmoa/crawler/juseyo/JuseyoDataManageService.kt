package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.DogBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.crawler.DataManageService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class JuseyoDataManageService : DataManageService {
    val logger = KotlinLogging.logger {}

    // TODO 분양완료 또는 무료로 주세요시 수집하지 않도록 해야함
    override fun checkDataIsNewAndParse(
        makeAdoptionDto: MakeAdoptionDto,
        latestAdoption: Adoption?,
    ): MakeAdoptionDto? {
        // 1) 생성 시간 추출
        // 정상적으로 파싱되지 않을 경우 데이터 수집을 하지 않는다.
        val createdAt: LocalDateTime = parseToLocalDateTime(makeAdoptionDto.createdAt) ?: return null
        // 처음으로 수집되는 데이터가 아니라면, 날짜 검사를 한다.
        latestAdoption?.let {
            // 수집된 데이터의 날짜가 DB에 있는 마지막 데이터의 날짜보다 이후일 때이만 수집한다.
            if (!createdAt.isAfter(it.createdAt)) {
                return null
            }
        }
        // 2) 간단한 정보 추출
        val region = Region.fromText(makeAdoptionDto.region)?.name ?: makeAdoptionDto.region
        val ageByMonth = parseAgeByMonth(makeAdoptionDto.age)
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

        return MakeAdoptionDto(
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
