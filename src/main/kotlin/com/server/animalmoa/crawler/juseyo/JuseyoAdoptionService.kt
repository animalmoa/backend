package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.CatBreed
import com.server.animalmoa.adoption.domain.DogBreed
import com.server.animalmoa.adoption.domain.Gender
import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.crawler.AdoptionService
import com.server.animalmoa.seq.PostSeq
import com.server.animalmoa.seq.SeqRepositoryService
import com.server.animalmoa.webdriver.UrlParser
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class JuseyoAdoptionService(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val seqRepositoryService: SeqRepositoryService,
    private val urlParser: UrlParser,
) : AdoptionService {
    private val sequenceIdentifier = "no"

    override fun saveAdoptionIfNotCrawled(makeAdoptionDto: MakeAdoptionDto): PostSeq? {
        // 1) DB에서 최신 Sequence 확인
        val seq = seqRepositoryService.findPostSeq(makeAdoptionDto.postType.name, Source.JUSEYO.name)
        // sequenceIdentifier 를 얻지 못 할시에 일단 DB의
        val newPostNumber =
            urlParser.extractQueryParam(
                makeAdoptionDto.originalUrl,
                sequenceIdentifier,
            ) ?: return seq
        if (ifDataIsAlreadyAdded(
                seq,
                newPostNumber,
            )
        ) {
            return null
        }
        parseDataAndSave(
            regionText = makeAdoptionDto.region,
            ageText = makeAdoptionDto.age.toString(),
            genderText = makeAdoptionDto.gender,
            speciesAndBreedText = makeAdoptionDto.species,
            thumbnailUrlText = makeAdoptionDto.thumbnailUrl,
            currentUrl = makeAdoptionDto.originalUrl,
            titleText = makeAdoptionDto.title,
            contentText = makeAdoptionDto.content,
            postType = makeAdoptionDto.postType,
            createdAtText = makeAdoptionDto.createdAt,
        )

        return seq.copy(
            sequence = newPostNumber,
        )
    }

    private fun ifDataIsAlreadyAdded(
        seq: PostSeq,
        newPostNumber: String,
    ) = seq.sequence.toInt() >= newPostNumber.toInt()

    override fun updateSequence(updatedSequence: PostSeq) {
        // seq 테이블 업데이트
        seqRepositoryService.updatePostSeq(updatedSequence)
    }

    private fun parseDataAndSave(
        regionText: String?,
        ageText: String?,
        genderText: String?,
        speciesAndBreedText: String?,
        thumbnailUrlText: String?,
        currentUrl: String,
        titleText: String?,
        contentText: String?,
        postType: PostType,
        createdAtText: String?,
    ) {
        // 1) 생성 시간 추출
        val createdAt = parseToLocalDateTime(createdAtText) ?: LocalDateTime.now()
        // 2) 간단한 정보 추출
        val region = Region.fromText(regionText)?.name ?: regionText // WIDE, SEOUL, CHUNGBUK, etc.
        val ageByMonth = parseAgeByMonth(ageText)
        val gender = Gender.fromText(genderText)?.name

        // 3) species, breed 결정
        val speciesText = speciesAndBreedText?.split("-")?.get(0)
        val breedText = speciesAndBreedText?.split("-")?.get(1)

        val species = Species.fromText(speciesText)?.name ?: speciesText
        val breed =
            when (species) {
                Species.DOG.name -> DogBreed.fromText(breedText)?.name
                Species.CAT.name -> CatBreed.fromText(breedText)?.name
                else -> breedText
            } ?: breedText

        adoptionRepositoryService.save(
            MakeAdoptionDto(
                species = species,
                breed = breed,
                region = region,
                gender = gender,
                age = ageByMonth.toString(),
                thumbnailUrl = thumbnailUrlText,
                originalUrl = currentUrl,
                source = Source.JUSEYO,
                title = titleText,
                content = contentText,
                postType = postType,
                createdAt = createdAt.toString(),
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
            println("Error parsing date: ${e.message}")
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
