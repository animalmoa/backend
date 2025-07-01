package com.server.animalmoa.crawler.crawler.source.ijoa

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.domain.AdoptionStatus
import com.server.animalmoa.common.adoption.domain.CatBreed
import com.server.animalmoa.common.adoption.domain.DogBreed
import com.server.animalmoa.common.adoption.domain.Gender
import com.server.animalmoa.common.adoption.domain.Region
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.adoption.domain.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import com.server.animalmoa.crawler.crawler.service.DataManager
import com.server.animalmoa.crawler.webdriver.UrlParser
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

@Service
class IjoaDataManageService(
    private val urlParser: UrlParser,
    private val adoptionRepositoryService: AdoptionRepositoryService,
) : DataManager(urlParser) {
    val logger = KotlinLogging.logger {}

    override fun processDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
        // 0) Identifier 추출 - URL에서 게시글 ID 추출
        val identifier = extractIdentifierFromUrl(rawDto.identifier ?: "")

        // 1) 날짜 변환
        val createdAt: LocalDateTime? = parseToLocalDateTime(rawDto.createdAt)

        // 2) 품종 처리
        val breedText = rawDto.breed
        val species = rawDto.species
        val breed =
            when (species) {
                Species.DOG.name -> DogBreed.fromSynonym(breedText)?.name
                Species.CAT.name -> CatBreed.fromSynonym(breedText)?.name
                else -> breedText
            } ?: breedText

        // 3) Adoption 객체 생성 및 저장
        val newAdoption =
            Adoption.from(
                MakeAdoptionDto(
                    species = species,
                    breed = breed,
                    region = Region.fromSynonym(rawDto.region).name,
                    gender = Gender.fromSynonym(rawDto.gender)?.name,
                    age = rawDto.age,
                    thumbnailUrl = rawDto.thumbnailUrl,
                    originalUrl = rawDto.originalUrl,
                    source = Source.IJOA,
                    title = rawDto.title,
                    content = rawDto.content,
                    postType = PostType.FREE_ADOPTION.name, // 기본값으로 FREE_ADOPTION 설정
                    adoptionStatus = AdoptionStatus.ING.name, // 기본값으로 ING 설정
                    createdAt = createdAt.toString(),
                    identifier = identifier,
                ),
            )

        return adoptionRepositoryService.ifExistUpdateElseSaveBySourceAndIdentifier(newAdoption)
    }

    // URL에서 게시글 ID 추출
    private fun extractIdentifierFromUrl(url: String): String {
        val regex = "/42/(\\d+)".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.getOrNull(1) ?: url
    }

    // 날짜 문자열을 LocalDateTime으로 변환
    // 상대적 시간 형식 (예: "1일전", "7시간전")을 처리
    fun parseToLocalDateTime(createdAtText: String?): LocalDateTime? {
        if (createdAtText.isNullOrBlank()) return null

        try {
            // 상대적 시간 형식 처리
            if (createdAtText.endsWith("전")) {
                return parseRelativeTime(createdAtText)
            }

            // 일반 날짜 형식 처리 (예: "yyyy-MM-dd HH:mm:ss")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return LocalDateTime.parse(createdAtText.trim(), formatter)
        } catch (e: DateTimeParseException) {
            logger.error("Error parsing date: ${e.message}")
            return null
        }
    }

    // 상대적 시간 형식 (예: "1일전", "7시간전")을 LocalDateTime으로 변환
    private fun parseRelativeTime(relativeTime: String): LocalDateTime {
        val now = LocalDateTime.now()
        
        // 숫자와 단위(일, 시간, 분, 초) 추출
        val pattern = Pattern.compile("(\\d+)([일시간분초])")
        val matcher = pattern.matcher(relativeTime)
        
        if (matcher.find()) {
            val amount = matcher.group(1).toInt()
            val unit = matcher.group(2)
            
            return when (unit) {
                "일" -> now.minusDays(amount.toLong())
                "시간" -> now.minusHours(amount.toLong())
                "분" -> now.minusMinutes(amount.toLong())
                "초" -> now.minusSeconds(amount.toLong())
                else -> now
            }
        }
        
        return now
    }
}