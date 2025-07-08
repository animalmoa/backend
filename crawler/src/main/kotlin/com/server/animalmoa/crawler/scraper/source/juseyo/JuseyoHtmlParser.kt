package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.domain.AdoptionStatus
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.adoption.domain.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.util.RegexUtil
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlParser
import mu.KotlinLogging
import okio.`-DeprecatedOkio`.source
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// 2025.05.24 Juseyo닷컴은 각정보에 대한 Xpath등이 너무 수시로 바뀌기 떄문에, 정규표현식으로 추출한다.
class JuseyoHtmlParser(
    var animalParam: String,
    var categoryParam: String,
    val species: Species,
) {
    val logger = KotlinLogging.logger {}

    // //////////// XPath

    // 카테고리 페이지의 Xpath
    val eachPostXpath = "//tr[@onclick]"

    // 각 무료 분양 글 페이지의 Xpath
    // 주세요 닷컴은 Xpath가 자주 달라져서 데이터 추출시 정규식을 주로 사용한다.
    val thumbnailXpath = "//*[@id='imgg1']/img"
    // /////////// End of XPath

    // ///////// property

    fun age(text: String) = RegexUtil.findFirstWordAfterKeyword(text, "개월수")

    // gender는 age와 한 줄에 존재한다.
    fun gender(text: String) = RegexUtil.findFirstWordAfterKeyword(text, "암수구분")

    fun region(text: String) = RegexUtil.findFirstWordAfterKeyword(text, "분양지역")

    // TODO 무료 분양 주세요와 원해요의 구분
    fun postType(text: String): PostType {
        val postType = RegexUtil.findFirstWordAfterKeyword(text, "")?.trim()
        return if (postType == null) {
            PostType.UNKNOWN
        } else if (postType.endsWith("무료분양")) {
            PostType.FREE_ADOPTION
        } else if (postType.endsWith("만원")) {
            PostType.RESPONSIBLE_ADOPTION
        } else {
            PostType.UNKNOWN
        }
    }

    // 분양동물 강아지 - 한국 고양이 [피해보상규정 자세히보기]
    fun breed(text: String): String? {
        val breedTexts = RegexUtil.findUntilKeyword(text, "분양동물", "[피해보상규정") ?: return null
        return breedTexts.substringAfter("-").trim()
    }

    // 등록일 : 2025.07.08 23:02:11
    fun createdAt(text: String): String? {
        // : 2025.07.08 23:02:11
        val createdAtTexts = RegexUtil.findWordsAfterKeyword(text, "등록일", 3)
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            return LocalDateTime.parse(createdAtTexts[1] + " " + createdAtTexts[2], formatter).toString()
        } catch (e: DateTimeParseException) {
            logger.error("Error parsing date: $createdAtTexts")
            return null
        }
    }

    // 내용 ~~~  ★사랑하는 반려동물이 좋은 주인을 만나 안전하게 살 수 있도록 아래의 사항을 꼭 지켜 주세요!!
    fun content(text: String): String? = RegexUtil.findUntilKeyword(text, "내용", "*사랑하는")

    // ///////// End of property

    fun getMakeAdoptionDto(
        html: String,
        url: String,
    ): MakeAdoptionDto {
        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()
        logger.info { "body: $bodyHtmlText" }

        return MakeAdoptionDto(
            originalUrl = url,
            title = content(bodyHtmlText)?.substringBefore("."),
            species = species.toString(),
            breed = breed(bodyHtmlText),
            region = region(bodyHtmlText),
            gender = gender(bodyHtmlText),
            content = content(bodyHtmlText),
            age = age(bodyHtmlText),
            thumbnailUrl = Source.JUSEYO.url + JsoupUtil.getImgSrcWithXpath(document, thumbnailXpath),
            postType = postType(bodyHtmlText),
            // TODO 전체 Img src를 검색해서 확인하는 특정 키워드로 끝나는지 확인하는 방법 분양완료시 = ok.jpg 분양중일시 idlog.gif
            adoptionStatus = AdoptionStatus.ING,
            createdAt = createdAt(bodyHtmlText),
            source = Source.JUSEYO,
            identifier = getIdentifier(url),
        )
    }

    companion object {
        fun dog(): JuseyoHtmlParser =
            JuseyoHtmlParser(
                "dog",
                "%B0%AD%BE%C6%C1%F6",
                Species.DOG,
            )

        fun cat(): JuseyoHtmlParser =
            JuseyoHtmlParser(
                "cat",
                "%B0%ED%BE%E7%C0%CC",
                Species.CAT,
            )

        fun getIdentifier(url: String) = UrlParser.extractQueryParam(url, "no")
    }
}
