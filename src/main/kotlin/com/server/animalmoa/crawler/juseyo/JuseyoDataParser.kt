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
import com.server.animalmoa.webdriver.UrlParser
import org.springframework.stereotype.Service

@Service
class JuseyoDataParser(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val urlParser: UrlParser,
) {
    private val checkSumName = "no"

    fun parseData(
        currentUrl: String,
        titleText: String?,
        contentText: String?,
        thumbnailUrlText: String?,
        animalTypeText: String?,
        regionText: String?,
        ageText: String?,
        genderText: String?,
        postType: PostType,
    ) {
        // 1) 간단한 정보 추출
        val postNumber = urlParser.extractQueryParam(currentUrl, checkSumName)
        val region = Region.fromText(regionText)?.name ?: regionText // WIDE, SEOUL, CHUNGBUK, etc.
        val ageByMonth = parseAgeByMonth(ageText)
        val gender = Gender.fromText(genderText ?: Gender.NOT_DECIDED.name)

        // 2) species, breed 결정
        val speciesText = animalTypeText.split("-")[0]
        val breedText = animalTypeText.split("-")[1]

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
                ageByMonth = ageByMonth,
                thumbnailUrl = thumbnailUrlText,
                originalUrl = currentUrl,
                source = Source.JUSEYO,
                title = titleText,
                content = contentText,
                postType = postType,
            ),
        )
    }

    private fun parseAgeByMonth(ageText: String): Int {
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
            else -> 0
        }
    }
}
